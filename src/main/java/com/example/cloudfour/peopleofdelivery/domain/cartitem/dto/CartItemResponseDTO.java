package com.example.cloudfour.peopleofdelivery.domain.cartitem.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

public class CartItemResponseDTO {
    @Getter
    @Builder
    public static class CartItemAddResponseDTO {
        UUID cartItemId;
        UUID cartId;
        UUID menuId;
        UUID menuOptionId;
        Integer quantity;
        Integer price;
    }

    @Getter
    @Builder
    public static class CartItemListResponseDTO {
        UUID cartItemId;
        UUID cartId;
        UUID menuId;
        UUID menuOptionId;
        Integer quantity;
        Integer price;
    }

    @Getter
    @Builder
    public static class CartItemUpdateResponseDTO {
        UUID cartItemId;
        UUID cartId;
        UUID menuOptionId;
        Integer quantity;
        Integer price;
    }

}
