package com.example.cloudfour.peopleofdelivery.domain.cart.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

public class CartResponseDTO {
    @Getter
    @Builder
    public static class CartCreateResponseDTO{
        UUID cartId;
        UUID userId;
        UUID storeId;
        LocalDateTime createdAt;
    }

    @Getter
    @Builder
    public static class CartDetailResponseDTO {
        UUID cartId;
        UUID userId;
        UUID storeId;
    }
}