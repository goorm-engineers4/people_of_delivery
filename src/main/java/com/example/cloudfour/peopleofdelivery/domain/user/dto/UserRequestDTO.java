package com.example.cloudfour.peopleofdelivery.domain.user.dto;

import com.example.cloudfour.peopleofdelivery.domain.region.entity.Region;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.LoginType;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

public class UserRequestDTO {
    @Getter
    @Builder
    public static class UserUpdateRequestDTO{
        String email;
        String nickname;
        String password;
        String number;
        Role role;
        LoginType loginType;
        String providerId;
    }

    @Getter
    @Builder
    public static class AddressRequestDTO {
        UUID userId;
        String address;
        Region region;
    }

}
