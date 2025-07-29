package com.example.cloudfour.peopleofdelivery.domain.cart.service.command;

import com.example.cloudfour.peopleofdelivery.domain.cart.dto.CartRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.cart.dto.CartResponseDTO;
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
public class CartCommandServicelmpl implements CartCommandService{
    @Override
    public CartResponseDTO.CartCreateResponseDTO createCart(CartRequestDTO.CartCreateRequestDTO CartCreateRequestDTO, User user) {
        return null;
    }

    @Override
    public void deleteCart(UUID cartId, User user) {
    }
}
