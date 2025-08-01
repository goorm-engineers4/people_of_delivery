package com.example.cloudfour.peopleofdelivery.domain.cart.converter;

import com.example.cloudfour.peopleofdelivery.domain.cart.dto.CartResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.cart.entity.Cart;

import java.util.UUID;

public class CartConverter {
    public static CartResponseDTO.CartDetailResponseDTO toCartDetailResponseDTO(Cart cart) {
        return CartResponseDTO.CartDetailResponseDTO.builder()
                .cartId(cart.getId())
                .userId(cart.getUser().getId())
                .storeId(cart.getStore().getId())
                .build();
    }

    public static CartResponseDTO.CartCreateResponseDTO toCartCreateResponseDTO(Cart cart, UUID cartItemId) {
        return CartResponseDTO.CartCreateResponseDTO.builder()
                .cartId(cart.getId())
                .userId(cart.getUser().getId())
                .storeId(cart.getStore().getId())
                .cartItemId(cartItemId)
                .createdAt(cart.getCreatedAt())
                .build();
    }

}
