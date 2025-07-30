package com.example.cloudfour.peopleofdelivery.domain.user.service;

import com.example.cloudfour.peopleofdelivery.domain.user.dto.UserRequestDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    public String getMyInfo(UUID id) {
        // Logic to retrieve user information
        return "User information retrieved successfully";
    }

    public String updateMyInfo(UUID id, UserRequestDTO.UserUpdateRequestDTO request) {
        // Logic to update user information
        return "User information updated successfully";
    }
    public String deleteAccount(UUID id) {
        // Logic to delete user account
        return "User account deleted successfully";
    }


}
