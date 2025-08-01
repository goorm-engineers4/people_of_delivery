package com.example.cloudfour.peopleofdelivery.domain.user.dto;

import com.example.cloudfour.peopleofdelivery.domain.region.entity.Region;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.LoginType;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

public class UserResponseDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class MeResponseDTO {
        private UUID userId;
        private String email;
        private String nickname;
        private String number;
        private Role role;
        private LoginType loginType;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class AddressResponseDTO {
        private UUID addressId;
        private String address;
        private UUID regionId;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class AddressListResponseDTO {
        private List<AddressResponseDTO> addresses;
    }
}