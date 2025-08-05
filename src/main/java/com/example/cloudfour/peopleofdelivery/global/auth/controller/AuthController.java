package com.example.cloudfour.peopleofdelivery.global.auth.controller;

import com.example.cloudfour.peopleofdelivery.global.auth.dto.AuthRequestDTO;
import com.example.cloudfour.peopleofdelivery.global.auth.dto.AuthResponseDTO;
import com.example.cloudfour.peopleofdelivery.global.auth.dto.TokenDto;
import com.example.cloudfour.peopleofdelivery.global.auth.service.AuthService;
import com.example.cloudfour.peopleofdelivery.global.auth.userdetails.CustomUserDetails;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/customer")
    @Operation(summary = "고객 로컬 회원가입", description = "고객 계정을 생성합니다.")
    public CustomResponse<AuthResponseDTO.AuthRegisterResponseDTO> registercustomer(
            @Valid @RequestBody AuthRequestDTO.RegisterRequestDto request) {
        var user = authService.registercustomer(request);
        return CustomResponse.onSuccess(HttpStatus.CREATED, user);
    }

    @PostMapping("/register/owner")
    @Operation(summary = "점주 로컬 회원가입", description = "점주 계정을 생성합니다.")
    public CustomResponse<AuthResponseDTO.AuthRegisterResponseDTO> registerowner(
            @Valid @RequestBody AuthRequestDTO.RegisterRequestDto request) {
        var user = authService.registerowner(request);
        return CustomResponse.onSuccess(HttpStatus.CREATED, user);
    }

    @PostMapping("/login")
    @Operation(summary = "로컬 로그인", description = "생성된 계정으로 로그인합니다.")
    public CustomResponse<TokenDto> login(@Valid @RequestBody AuthRequestDTO.LoginRequestDto request) {
        TokenDto token = authService.login(request);
        return CustomResponse.onSuccess(HttpStatus.OK, token);
    }

    @PostMapping("/password")
    @Operation(summary = "비밀번호 재설정", description = "비밀번호를 재설정합니다.")
    public CustomResponse<Void> changePassword(
            @Valid @RequestBody AuthRequestDTO.PasswordChangeDto request,
            @AuthenticationPrincipal CustomUserDetails user) {
        authService.changePassword(user.getId(), request);
        return CustomResponse.onSuccess(null);
    }

    @PostMapping("/email/send")
    @Operation(summary = "이메일 인증 전송", description = "입력된 계정으로 인증 이메일을 전송합니다.")
    public CustomResponse<Void> sendEmail(@Valid @RequestBody AuthRequestDTO.EmailVerifyRequestDTO request) {
        authService.sendVerificationEmail(request.email());
        return CustomResponse.onSuccess(null);
    }

    @PostMapping("/email/verify")
    @Operation(summary = "이메일 인증 검증", description = "입력된 이메일의 인증 코드를 검증합니다.")
    public CustomResponse<Void> verifyEmail(@Valid @RequestBody AuthRequestDTO.EmailVerifyRequestDTO request) {
        authService.verifyEmailCode(request);
        return CustomResponse.onSuccess(null);
    }

    @PostMapping("/email/change/start")
    @Operation(summary = "이메일 수정", description = "새로 입력된 이메일로 이메일을 수정합니다.")
    public CustomResponse<Void> startEmailChange(@AuthenticationPrincipal CustomUserDetails user,
                                                 @Valid @RequestBody AuthRequestDTO.EmailChangeStartRequest req) {
        authService.startEmailChange(user.getId(), req.newEmail());
        return CustomResponse.onSuccess(null);
    }

    @PostMapping("/email/change/verify")
    @Operation(summary = "수정된 이메일 검증", description = "수정된 이메일의 인증 코드를 검증합니다.")
    public CustomResponse<Void> verifyEmailChange(@AuthenticationPrincipal CustomUserDetails user,
                                                  @Valid @RequestBody AuthRequestDTO.EmailChangeVerifyRequest req) {
        authService.verifyEmailChange(user.getId(), req.newEmail(), req.code());
        return CustomResponse.onSuccess(null);
    }
}