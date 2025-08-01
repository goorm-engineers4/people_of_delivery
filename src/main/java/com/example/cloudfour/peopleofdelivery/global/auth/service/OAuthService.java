package com.example.cloudfour.peopleofdelivery.global.auth.service;

import com.example.cloudfour.peopleofdelivery.global.auth.dto.OAuthRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.LoginType;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.Role;
import com.example.cloudfour.peopleofdelivery.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class OAuthService {

    private final UserRepository userRepository;

    public void completeSignup(OAuthRequestDTO.AdditionalSignupRequestDto dto) {
        // 이미 가입된 이메일이면 예외 처리
        if (userRepository.findByEmailAndDeletedFalse(dto.getEmail()).isPresent()) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }

        User user = User.builder()
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .number(dto.getNumber())
                .loginType(LoginType.GOOGLE)
                .providerId(dto.getProviderId())
                .role(Role.CUSTOMER)
                .build();

        userRepository.save(user);
    }

}
