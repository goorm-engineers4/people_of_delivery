package com.example.cloudfour.peopleofdelivery.global.auth.dto;

import com.example.cloudfour.peopleofdelivery.domain.user.enums.LoginType;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

public class AuthRequestDTO {

    @Getter
    @Builder
    public static class RegisterRequestDto {
        UUID userId;
        String email;
        String nickname;
        String password;
        String number;
        Role role;
        LoginType loginType;
    }

    @Getter
    @Builder
    public static class LoginRequestDto {
        String email;
        String password;
    }

    @Getter
    @Builder
    public static class PasswordChangeDto {
        String currentPassword;
        String newPassword;
    }

    @Getter
    @Builder
    public static class EmailSendRequestDTO {
        String email;
    }

    @Getter
    @Builder
    public static class EmailVerifyRequestDTO {
        String email;
        String code;
    }
}
