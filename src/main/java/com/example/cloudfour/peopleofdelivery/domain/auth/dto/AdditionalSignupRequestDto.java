package com.example.cloudfour.peopleofdelivery.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalSignupRequestDto {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String number;

    @NotBlank
    private String nickname;

    @NotBlank
    private String providerId; // OAuth2 'sub' 값
}

