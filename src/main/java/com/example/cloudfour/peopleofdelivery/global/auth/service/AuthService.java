package com.example.cloudfour.peopleofdelivery.global.auth.service;

import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.LoginType;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.Role;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.VerificationPurpose;
import com.example.cloudfour.peopleofdelivery.global.auth.dto.AuthRequestDTO;
import com.example.cloudfour.peopleofdelivery.global.auth.dto.AuthResponseDTO;
import com.example.cloudfour.peopleofdelivery.global.auth.dto.TokenDto;
import com.example.cloudfour.peopleofdelivery.domain.user.repository.UserRepository;
import com.example.cloudfour.peopleofdelivery.global.auth.entity.VerificationCode;
import com.example.cloudfour.peopleofdelivery.global.auth.jwt.JwtTokenProvider;
import com.example.cloudfour.peopleofdelivery.global.auth.repository.VerificationCodeRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final VerificationCodeRepository verificationCodeRepository;

    private static final int CODE_LEN = 6;
    private static final int CODE_EXP_MIN = 10;

    public AuthResponseDTO.AuthRegisterResponseDTO register(AuthRequestDTO.RegisterRequestDto request){
        String email = request.email().toLowerCase();
        if (userRepository.existsByEmailAndIsDeletedFalse(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        User user = User.builder()
                .email(email)
                .nickname(request.nickname())
                .password(passwordEncoder.encode(request.password()))
                .number(request.number())
                .role(Role.CUSTOMER)           // 초기 기본권한
                .loginType(LoginType.LOCAL)
                .emailVerified(false)
                .build();

        userRepository.save(user);

        return new AuthResponseDTO.AuthRegisterResponseDTO(user.getId(), user.getEmail(), user.getNickname());
    }

    public TokenDto login(AuthRequestDTO.LoginRequestDto request) {
        String email = request.email().toLowerCase();

        User user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // loginType 확인(LOCAL만 허용)
        if (user.getLoginType() != LoginType.LOCAL) {
            throw new IllegalArgumentException("소셜 계정은 로컬 로그인 불가");
        }
        if (user.getIsDeleted()) {
            throw new IllegalArgumentException("이미 탈퇴한 계정입니다.");
        }
        if (!user.isEmailVerified()) {
            throw new IllegalArgumentException("이메일 인증이 필요합니다.");
        }

        return jwtTokenProvider.createToken(
                user.getId(), user.getRole()
        );
    }

    public void changePassword(UUID userId, AuthRequestDTO.PasswordChangeDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (user.getLoginType() != LoginType.LOCAL) {
            throw new IllegalArgumentException("소셜 로그인 계정은 비밀번호 변경 대상이 아닙니다.");
        }

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        user.changePassword(passwordEncoder.encode(request.newPassword()));
    }

    public void sendVerificationEmail(String email) {
        Objects.requireNonNull(email, "email must not be null");

        String code = generateCode(CODE_LEN); // 6자리 숫자
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(CODE_EXP_MIN); // 10분

        verificationCodeRepository.deleteByEmail(email);
        verificationCodeRepository.save(
                VerificationCode.builder()
                        .email(email)
                        .code(code)
                        .expiryDate(expiry)
                        .purpose(VerificationPurpose.EMAIL_VERIFY)
                        .build()
        );

        String title = "이메일 인증 번호";
        String content = "<html>"
                + "<body>"
                + "<h1> 인증 코드 : " + code + "</h1>"
                + "<p>해당 코드를 홈페이지에 입력하세요.</p>"
                + "<p> * 본 메일은 자동응답 메일이므로 본 메일에 회신하지 마시기 바랍니다.</p>"
                +"</footer>"
                + "</body>"
                + "</html>";
        try{
            emailService.sendSimpleMessage(email, title, content);
        }catch(RuntimeException | MessagingException e){
            e.printStackTrace();
            throw new RuntimeException("Unable to send email in sendEmail", e);
        }
    }

    public void verifyEmailCode(AuthRequestDTO.EmailVerifyRequestDTO request) {
        Objects.requireNonNull(request, "rnull 안 됨l");
        Objects.requireNonNull(request.email(), "email null 안 됨");
        Objects.requireNonNull(request.code(), "code null 안 됨");

        VerificationCode vc = verificationCodeRepository
                .findByEmailAndCode(request.email(), request.code())
                .orElseThrow(() -> new IllegalArgumentException("정확하지 않은 인증코드입니다."));
        if (vc.isExpired()) {
            verificationCodeRepository.delete(vc);
            throw new IllegalArgumentException("만료된 인증코드입니다.");
        }
        verificationCodeRepository.delete(vc);

        // 이메일 인증 완료 반영
        userRepository.findByEmailAndIsDeletedFalse(request.email())
                .ifPresent(User::markEmailVerified);
    }

    public void startEmailChange(UUID userId, String newEmail) {
        User u = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (u.getEmail().equalsIgnoreCase(newEmail)) {
            throw new IllegalArgumentException("기존 이메일과 동일합니다.");
        }
        if (userRepository.existsByEmailAndIsDeletedFalse(newEmail)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        u.requestEmailChange(newEmail);

        verificationCodeRepository.deleteByEmailAndPurpose(newEmail, VerificationPurpose.CHANGE_EMAIL);

        var code = generateCode(CODE_LEN);
        verificationCodeRepository.save(
                VerificationCode.builder()
                        .email(newEmail)
                        .code(code)
                        .purpose(VerificationPurpose.CHANGE_EMAIL)
                        .expiryDate(LocalDateTime.now().plusMinutes(CODE_EXP_MIN))
                        .build()
        );

        String title = "이메일 인증 번호";
        String content = "<html>"
                + "<body>"
                + "<h1> 인증 코드 : " + code + "</h1>"
                + "<p>해당 코드를 홈페이지에 입력하세요.</p>"
                + "<p> * 본 메일은 자동응답 메일이므로 본 메일에 회신하지 마시기 바랍니다.</p>"
                +"</footer>"
                + "</body>"
                + "</html>";
        try{
            emailService.sendSimpleMessage(newEmail, title, content);
        }catch(RuntimeException | MessagingException e){
            e.printStackTrace();
            throw new RuntimeException("Unable to send email in sendEmail", e);
        }
    }

    public void verifyEmailChange(UUID userId, String newEmail, String code) {
        User u = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (u.getPendingEmail() == null || !u.getPendingEmail().equalsIgnoreCase(newEmail)) {
            throw new IllegalArgumentException("변경 대기 중인 이메일이 일치하지 않습니다.");
        }

        var vc = verificationCodeRepository
                .findByEmailAndCodeAndPurpose(newEmail, code, VerificationPurpose.CHANGE_EMAIL)
                .orElseThrow(() -> new IllegalArgumentException("인증 코드가 올바르지 않습니다."));

        if (vc.isExpired()) {
            verificationCodeRepository.delete(vc);
            throw new IllegalArgumentException("인증 코드가 만료되었습니다.");
        }

        if (userRepository.existsByEmailAndIsDeletedFalse(newEmail)) {
            throw new IllegalStateException("확정 중 충돌: 이미 사용 중인 이메일입니다.");
        }

        u.confirmEmailChange();
        verificationCodeRepository.delete(vc);

        // 리프레시 토큰/세션 무효화 정책 적용 ??
    }

    private String generateCode(int len) {
        var r = new Random();
        var sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb.append(r.nextInt(10));
        return sb.toString();
    }
}
