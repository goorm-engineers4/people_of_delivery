package com.example.cloudfour.peopleofdelivery.global.auth.controller;

import com.example.cloudfour.peopleofdelivery.global.auth.dto.AuthRequestDTO;
import com.example.cloudfour.peopleofdelivery.global.auth.dto.TokenDto;
import com.example.cloudfour.peopleofdelivery.global.auth.service.AuthService;
import com.example.cloudfour.peopleofdelivery.global.auth.userdetails.CustomUserDetails;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public CustomResponse<Void> register(@RequestBody AuthRequestDTO.RegisterRequestDto request) {
        authService.register(request);
        return CustomResponse.onSuccess(HttpStatus.CREATED, null);
    }

    @PostMapping("/login")
    public CustomResponse<TokenDto> login(@RequestBody AuthRequestDTO.LoginRequestDto request) {
        TokenDto token = authService.login(request);
        return CustomResponse.onSuccess(HttpStatus.OK, token);
    }

    @PostMapping("/password")
    public CustomResponse<Void> changePassword(@RequestBody AuthRequestDTO.PasswordChangeDto request,
                                               @AuthenticationPrincipal CustomUserDetails user) {
        authService.changePassword(user.getUser().getId(), request);
        return CustomResponse.onSuccess(null);
    }

    @PostMapping("/email/send")
    public CustomResponse<Void> sendEmail(@RequestBody AuthRequestDTO.EmailSendRequestDTO request) {
        authService.sendVerificationEmail(request.getEmail());
        return CustomResponse.onSuccess(null);
    }

    @PostMapping("/email/verify")
    public CustomResponse<Void> verifyEmail(@RequestBody AuthRequestDTO.EmailVerifyRequestDTO request) {
        authService.verifyEmailCode(request);
        return CustomResponse.onSuccess(null);
    }


}
