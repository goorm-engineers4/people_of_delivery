package com.example.cloudfour.peopleofdelivery.domain.cartitem.converter;

import com.example.cloudfour.peopleofdelivery.domain.cart.dto.CartRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.dto.CartItemRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.dto.CartItemResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.entity.CartItem;

import java.util.UUID;

public class CartItemConverter {

    public static CartItemResponseDTO.CartItemAddResponseDTO toCartItemAddResponseDTO(CartItem cartItem) {
        return CartItemResponseDTO.CartItemAddResponseDTO.builder()
                .cartItemId(cartItem.getId())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getPrice())
                .menuId(cartItem.getMenu().getId())
                .menuOptionId(cartItem.getMenuOption().getId())
                .build();
    }

    public static CartItemResponseDTO.CartItemUpdateResponseDTO toCartItemUpdateResponseDTO(CartItem cartItem, UUID menuId, UUID menuOptionId) {
        return CartItemResponseDTO.CartItemUpdateResponseDTO.builder()
                .cartItemId(cartItem.getId())
                .cartId(cartItem.getCart().getId())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getPrice())
                .menuId(menuId)
                .menuOptionId(menuOptionId)
                .build();

    }

    public static CartItemResponseDTO.CartItemListResponseDTO toCartItemListResponseDTO(CartItem cartItem) {
        return CartItemResponseDTO.CartItemListResponseDTO.builder()
                .cartItemId(cartItem.getId())
                .cartId(cartItem.getCart().getId())
                .menuId(cartItem.getMenu().getId())
                .menuOptionId(cartItem.getMenuOption().getId())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getPrice())
                .build();
    }

    public static CartItemRequestDTO.CartItemAddRequestDTO toCartItemAddRequestDTO(CartRequestDTO.CartCreateRequestDTO cartCreateRequestDTO, int price){
        return   CartItemRequestDTO.CartItemAddRequestDTO.builder()
                .menuId(cartCreateRequestDTO.getMenuId())
                .menuOptionId(cartCreateRequestDTO.getMenuOptionId())
                .quantity(1)
                .price(price)
                .build();
    }
}