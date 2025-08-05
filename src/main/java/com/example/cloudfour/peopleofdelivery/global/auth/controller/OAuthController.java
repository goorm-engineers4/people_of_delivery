package com.example.cloudfour.peopleofdelivery.global.auth.controller;

import com.example.cloudfour.peopleofdelivery.global.auth.dto.OAuthRequestDTO;
import com.example.cloudfour.peopleofdelivery.global.auth.service.OAuthService;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "소셜 회원가입 또는 로그인", description = "google 아이디로 회원가입 또는 로그인합니다.")
    public CustomResponse<Void> completeSignup(@RequestBody OAuthRequestDTO.AdditionalSignupRequestDto dto) {
        oauthService.completeSignup(dto);
        return CustomResponse.onSuccess(HttpStatus.CREATED, null);
    }
}

