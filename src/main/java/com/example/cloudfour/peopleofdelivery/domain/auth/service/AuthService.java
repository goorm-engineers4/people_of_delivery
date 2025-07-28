package com.example.cloudfour.peopleofdelivery.domain.auth.service;

import com.example.cloudfour.peopleofdelivery.domain.auth.dto.AdditionalSignupRequestDto;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.LoginType;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.Role;
import com.example.cloudfour.peopleofdelivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public void completeSignup(AdditionalSignupRequestDto dto) {
        // 이미 가입된 이메일이면 예외 처리
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }

        User user = User.builder()
                .nickname(dto.getNickname())
                .number(dto.getNumber())
                .loginType(LoginType.GOOGLE)
                .providerId(dto.getProviderId())
                .role(Role.CUSTMER)
                .build();

        userRepository.save(user);
    }
}
