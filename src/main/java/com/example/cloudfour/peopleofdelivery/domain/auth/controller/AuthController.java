package com.example.cloudfour.peopleofdelivery.domain.auth.controller;

import com.example.cloudfour.peopleofdelivery.domain.auth.dto.LoginRequestDto;
import com.example.cloudfour.peopleofdelivery.domain.auth.dto.PasswordChangeDto;
import com.example.cloudfour.peopleofdelivery.domain.auth.dto.RegisterRequestDto;
import com.example.cloudfour.peopleofdelivery.domain.auth.dto.TokenDto;
import com.example.cloudfour.peopleofdelivery.domain.auth.service.AuthService;
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
    public CustomResponse<Void> register(@RequestBody RegisterRequestDto request) {
        authService.register(request);
        return CustomResponse.onSuccess(HttpStatus.CREATED, null);
    }

    @PostMapping("/login")
    public CustomResponse<TokenDto> login(@RequestBody LoginRequestDto request) {
        TokenDto token = authService.login(request);
        return CustomResponse.onSuccess(HttpStatus.OK, token);
    }

    @PostMapping("/password")
    public CustomResponse<Void> changePassword(@RequestBody PasswordChangeDto request,
                                               @AuthenticationPrincipal CustomUserDetails user) {
        authService.changePassword(user.getUser().getId(), request);
        return CustomResponse.onSuccess(null);
    }


}
