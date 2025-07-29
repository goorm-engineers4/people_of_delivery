package com.example.cloudfour.peopleofdelivery.domain.cartitem.service.command;

import com.example.cloudfour.peopleofdelivery.domain.cartitem.dto.CartItemRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.dto.CartItemResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CartItemCommandService {
    public CartItemResponseDTO.CartItemUpdateResponseDTO updateCartItem(CartItemRequestDTO.CartItemUpdateRequestDTO request, UUID cartItemId, User user) {
        return null;
    }

    public void deleteCartItem(UUID cartItemId, User user) {
    }
}
