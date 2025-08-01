package com.example.cloudfour.peopleofdelivery.global.auth.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

public class OAuthRequestDTO {
    @Getter
    public static class AdditionalSignupRequestDto {
        @Email
        @NotBlank
        private String email;

        @NotBlank
        private String number;

        @NotBlank
        private String nickname;

        @NotBlank
        private String providerId;  // OAuth2 'sub' 값
    }
}