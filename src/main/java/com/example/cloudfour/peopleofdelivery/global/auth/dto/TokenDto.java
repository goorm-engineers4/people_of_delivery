package com.example.cloudfour.peopleofdelivery.global.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenDto {
    private final String grantType;
    private final String accessToken;
    private final String refreshToken;
    private final Long accessTokenExpiresIn;
}
