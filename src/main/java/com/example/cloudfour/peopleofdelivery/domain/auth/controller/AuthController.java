package com.example.cloudfour.peopleofdelivery.domain.auth.controller;

import com.example.cloudfour.peopleofdelivery.domain.auth.dto.AdditionalSignupRequestDto;
import com.example.cloudfour.peopleofdelivery.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/complete-profile")
    public ResponseEntity<Void> completeSignup(@RequestBody @Valid AdditionalSignupRequestDto dto) {
        authService.completeSignup(dto);
        return ResponseEntity.ok().build();
    }
}
