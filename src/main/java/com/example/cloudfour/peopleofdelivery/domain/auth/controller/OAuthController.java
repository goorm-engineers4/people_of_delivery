package com.example.cloudfour.peopleofdelivery.domain.auth.controller;

import com.example.cloudfour.peopleofdelivery.domain.auth.dto.*;
import com.example.cloudfour.peopleofdelivery.domain.auth.service.AuthService;
import com.example.cloudfour.peopleofdelivery.domain.auth.service.OAuthService;
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
    public CustomResponse<Void> completeSignup(@RequestBody OAuthRequestDTO.AdditionalSignupRequestDto dto) {
        oauthService.completeSignup(dto);
        return CustomResponse.onSuccess(HttpStatus.CREATED, null);
    }
}

