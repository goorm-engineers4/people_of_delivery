package com.example.cloudfour.peopleofdelivery.global.auth.dto;


import lombok.Builder;
import lombok.Getter;

public class OAuthRequestDTO {
    @Getter
    @Builder
    public static class AdditionalSignupRequestDto {
        String email;
        String number;
        String nickname;
        String providerId; // OAuth2 'sub' 값
    }
}