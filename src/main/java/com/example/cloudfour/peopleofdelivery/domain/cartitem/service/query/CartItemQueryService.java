package com.example.cloudfour.peopleofdelivery.domain.cartitem.service.query;

import com.example.cloudfour.peopleofdelivery.domain.cartitem.converter.CartItemConverter;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.dto.CartItemResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.entity.CartItem;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.exception.CartItemErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.exception.CartItemException;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.repository.CartItemRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CartItemQueryService {
    private final CartItemRepository cartItemRepository;

    public CartItemResponseDTO.CartItemListResponseDTO getCartItemById(UUID cartItemId, User user) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(()->new CartItemException(CartItemErrorCode.NOT_FOUND));
        return CartItemConverter.toCartItemListResponseDTO(cartItem);
    }

    public List<CartItemResponseDTO.CartItemListResponseDTO>getCartItemList(UUID cartId, User user) {
        List<CartItem> cartItem = cartItemRepository.findAllByCartId(cartId);
        if(cartItem.isEmpty()){
            throw new CartItemException(CartItemErrorCode.NOT_FOUND);
        }
        return cartItem.stream().map(CartItemConverter::toCartItemListResponseDTO).toList();
    }
}
