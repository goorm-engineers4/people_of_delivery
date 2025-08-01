package com.example.cloudfour.peopleofdelivery.global.auth.dto;

import com.example.cloudfour.peopleofdelivery.domain.user.enums.LoginType;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

public class AuthResponseDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class AuthRegisterResponseDTO {
        private UUID userId;
        private String email;
        // 비밀번호는 보안상 응답에 포함하지 않음
        private String nickname;
    }
}
