package com.example.cloudfour.peopleofdelivery.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class OAuthRequestDTO {
    @Getter
    @Builder
    public class AdditionalSignupRequestDto {
        @Email
        @NotBlank
        String email;

        @NotBlank
        String number;

        @NotBlank
        String nickname;

        @NotBlank
        String providerId; // OAuth2 'sub' 값
    }
}
