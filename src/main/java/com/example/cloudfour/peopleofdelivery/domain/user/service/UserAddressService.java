package com.example.cloudfour.peopleofdelivery.domain.user.service;

import com.example.cloudfour.peopleofdelivery.domain.user.dto.UserRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.user.dto.UserResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAddressService {
    public void addAddress(UUID userId, UserRequestDTO.AddressRequestDTO request) {
        // Logic to add an address for the user
    }

    public List<UserResponseDTO.AddressResponseDTO> getAddresses(UUID userId) {
        // Logic to retrieve the user's address
        return null;
    }
    public void updateAddress(UUID userId, UUID addressId, UserRequestDTO.AddressRequestDTO request) {
        // Logic to update the user's addres
    }

    public void softDeleteAddress(UUID userId, UUID addressId) {
        // Logic to delete the user's address
    }
}
