package com.example.cloudfour.peopleofdelivery.domain.cart.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

public class CartRequestDTO {
    @Getter
    @Builder
    public static class CartCreateRequestDTO{
        UUID storeId;
        UUID menuId;
        UUID menuOptionId;
    }
}
