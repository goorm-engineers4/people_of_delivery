package com.example.cloudfour.peopleofdelivery.domain.cart.service.query;

import com.example.cloudfour.peopleofdelivery.domain.cart.converter.CartConverter;
import com.example.cloudfour.peopleofdelivery.domain.cart.dto.CartResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.cart.entity.Cart;
import com.example.cloudfour.peopleofdelivery.domain.cart.exception.CartErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.cart.exception.CartException;
import com.example.cloudfour.peopleofdelivery.domain.cart.repository.CartRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartQueryService {
    private final CartRepository cartRepository;

    public CartResponseDTO.CartDetailResponseDTO getCartListById(UUID cartId, User user) {
        Cart cart = cartRepository.findByIdAndUser(cartId, user.getId())
                .orElseThrow(() -> new CartException(CartErrorCode.NOT_FOUND));
        return CartConverter.toCartDetailResponseDTO(cart);
    }
}
