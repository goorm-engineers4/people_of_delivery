package com.example.cloudfour.peopleofdelivery.domain.auth.service;

import com.example.cloudfour.peopleofdelivery.domain.auth.dto.AuthRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.auth.dto.TokenDto;
import com.example.cloudfour.peopleofdelivery.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;

    public void register(AuthRequestDTO.RegisterRequestDto request){}

    public TokenDto login(AuthRequestDTO.LoginRequestDto request){
        return null;
    }

    public void changePassword(UUID userId, AuthRequestDTO.PasswordChangeDto request) {
    }

    public void sendVerificationEmail(String email){

    }


    public void verifyEmailCode(AuthRequestDTO.EmailVerifyRequestDTO request) {
        // 이메일 인증 코드 전송 로직
    }


}
