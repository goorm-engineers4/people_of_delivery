package com.example.cloudfour.peopleofdelivery.global.auth.dto;

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
        private String nickname;
    }
}
