package com.example.cloudfour.peopleofdelivery.domain.auth.controller;

import com.example.cloudfour.peopleofdelivery.domain.auth.dto.AdditionalSignupRequestDto;
import com.example.cloudfour.peopleofdelivery.domain.auth.dto.EmailSendRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.auth.dto.EmailVerifyRequestDTO;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oauthService;

    @PostMapping("/signup")
    public CustomResponse<Void> completeSignup(@RequestBody AdditionalSignupRequestDto dto) {
        oauthService.completeSignup(dto);
        return CustomResponse.onSuccess(HttpStatus.CREATED, null);
    }

    @PostMapping("/email/send")
    public CustomResponse<Void> sendEmail(@RequestBody EmailSendRequestDTO request) {
        oauthService.sendVerificationEmail(request.getEmail());
        return CustomResponse.onSuccess(null);
    }

    @PostMapping("/email/verify")
    public CustomResponse<Void> verifyEmail(@RequestBody EmailVerifyRequestDTO request) {
        oauthService.verifyEmailCode(request);
        return CustomResponse.onSuccess(null);
    }
}

