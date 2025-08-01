package com.example.cloudfour.peopleofdelivery.domain.user.service;

import com.example.cloudfour.peopleofdelivery.domain.user.dto.UserResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDTO.MeResponseDTO getMyInfo(UUID userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return UserResponseDTO.MeResponseDTO.builder()
                .userId(u.getId())
                .email(u.getEmail())
                .nickname(u.getNickname())
                .number(u.getNumber())
                .role(u.getRole())
                .loginType(u.getLoginType())
                .build();
    }

    @Transactional
    public void updateProfile(UUID userId, String nickname, String number) {
        User u = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (nickname != null && !nickname.isBlank() && !nickname.equals(u.getNickname())) {
            u.changeNickname(nickname);
        }
        if (number != null && !number.isBlank() && !number.equals(u.getNumber())) {
            u.changeNumber(number);
        }
    }

    @Transactional
    public void deleteAccount(UUID userId) {
        User u = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        u.softDelete();
    }
}


