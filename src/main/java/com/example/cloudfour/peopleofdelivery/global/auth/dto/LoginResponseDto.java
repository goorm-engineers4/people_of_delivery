package com.example.cloudfour.peopleofdelivery.global.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private String accessToken;
    private String refreshToken;
    private boolean needsAdditionalInfo;
}
