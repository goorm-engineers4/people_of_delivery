package com.example.cloudfour.peopleofdelivery.global.auth.controller;

import com.example.cloudfour.peopleofdelivery.global.auth.dto.AuthRequestDTO;
import com.example.cloudfour.peopleofdelivery.global.auth.dto.AuthResponseDTO;
import com.example.cloudfour.peopleofdelivery.global.auth.dto.TokenDto;
import com.example.cloudfour.peopleofdelivery.global.auth.service.AuthService;
import com.example.cloudfour.peopleofdelivery.global.auth.userdetails.CustomUserDetails;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.CustomResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public CustomResponse<AuthResponseDTO.AuthRegisterResponseDTO> register(
            @Valid @RequestBody AuthRequestDTO.RegisterRequestDto request) {
        var user = authService.register(request);
        return CustomResponse.onSuccess(HttpStatus.CREATED, user);
    }

    @PostMapping("/login")
    public CustomResponse<TokenDto> login(@Valid @RequestBody AuthRequestDTO.LoginRequestDto request) {
        TokenDto token = authService.login(request);
        return CustomResponse.onSuccess(HttpStatus.OK, token);
    }

    @PostMapping("/password")
    public CustomResponse<Void> changePassword(
            @Valid @RequestBody AuthRequestDTO.PasswordChangeDto request,
            @AuthenticationPrincipal CustomUserDetails user) {
        authService.changePassword(user.getId(), request);
        return CustomResponse.onSuccess(null);
    }

    @PostMapping("/email/send")
    public CustomResponse<Void> sendEmail(@Valid @RequestBody AuthRequestDTO.EmailVerifyRequestDTO request) {
        authService.sendVerificationEmail(request.email());
        return CustomResponse.onSuccess(null);
    }

    @PostMapping("/email/verify")
    public CustomResponse<Void> verifyEmail(@Valid @RequestBody AuthRequestDTO.EmailVerifyRequestDTO request) {
        authService.verifyEmailCode(request);
        return CustomResponse.onSuccess(null);
    }

    @PostMapping("/email/change/start")
    public CustomResponse<Void> startEmailChange(@AuthenticationPrincipal CustomUserDetails user,
                                                 @Valid @RequestBody AuthRequestDTO.EmailChangeStartRequest req) {
        authService.startEmailChange(user.getId(), req.newEmail());
        return CustomResponse.onSuccess(null);
    }

    @PostMapping("/email/change/verify")
    public CustomResponse<Void> verifyEmailChange(@AuthenticationPrincipal CustomUserDetails user,
                                                  @Valid @RequestBody AuthRequestDTO.EmailChangeVerifyRequest req) {
        authService.verifyEmailChange(user.getId(), req.newEmail(), req.code());
        return CustomResponse.onSuccess(null);
    }
}