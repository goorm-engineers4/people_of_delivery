package com.example.cloudfour.peopleofdelivery.domain.user.dto;

import com.example.cloudfour.peopleofdelivery.domain.region.entity.Region;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

public class UserResponseDTO {
    @Getter
    @Builder
    public static class AddressResponseDTO {
        UUID userId;
        String address;
        Region region;
    }
}
