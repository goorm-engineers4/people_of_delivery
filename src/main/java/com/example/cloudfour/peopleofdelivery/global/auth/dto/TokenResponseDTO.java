package com.example.cloudfour.peopleofdelivery.global.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenResponseDTO {
    private final String accessToken;
    private final String refreshToken;

    @Builder
    public TokenResponseDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
