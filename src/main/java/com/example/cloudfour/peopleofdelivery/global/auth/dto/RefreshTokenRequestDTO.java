package com.example.cloudfour.peopleofdelivery.global.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RefreshTokenRequestDTO {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String refreshToken;
}
