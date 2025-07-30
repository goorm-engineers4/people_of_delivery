package com.example.cloudfour.peopleofdelivery.domain.cart.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class CartResponseDTO {
    public static class CartCreateResponseDTO{
        UUID cartId;
        UUID userId;
        UUID storeId;
        LocalDateTime createdAt;
    }
}