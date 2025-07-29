package com.example.cloudfour.peopleofdelivery.domain.cartitem.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class CartItemRequestDTO {

    @Getter
    @Setter
    public static class CartItemUpdateRequestDTO {
        private UUID menuId;
        private UUID menuOptionId;
        private Integer quantity;
        private Integer price;
    }
}
