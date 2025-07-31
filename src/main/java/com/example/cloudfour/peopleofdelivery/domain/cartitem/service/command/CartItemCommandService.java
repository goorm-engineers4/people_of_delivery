package com.example.cloudfour.peopleofdelivery.domain.cartitem.service.command;

import com.example.cloudfour.peopleofdelivery.domain.cart.entity.Cart;
import com.example.cloudfour.peopleofdelivery.domain.cart.repository.CartRepository;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.converter.CartItemConverter;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.dto.CartItemRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.dto.CartItemResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.entity.CartItem;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.exception.CartItemErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.exception.CartItemException;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.repository.CartItemRepository;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.Menu;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuOption;
import com.example.cloudfour.peopleofdelivery.domain.menu.repository.MenuOptionRepository;
import com.example.cloudfour.peopleofdelivery.domain.menu.repository.MenuRepository;
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
    CartItemRepository cartItemRepository;
    CartRepository cartRepository;
    MenuRepository menuRepository;
    MenuOptionRepository menuOptionRepository;

    public CartItemResponseDTO.CartItemAddResponseDTO AddCartItem(CartItemRequestDTO.CartItemAddRequestDTO cartItemAddRequestDTO, UUID cartId, User user) {
        Cart cart = cartRepository.findByIdAndUser(cartId, user.getId())
                .orElseThrow(() -> new CartItemException(CartItemErrorCode.NOT_FOUND));
        Menu menu = menuRepository.findById(cartItemAddRequestDTO.getMenuId())
                .orElseThrow(() -> new CartItemException(CartItemErrorCode.NOT_FOUND));
        MenuOption menuOption = menuOptionRepository.findById(cartItemAddRequestDTO.getMenuOptionId())
                .orElseThrow(() -> new CartItemException(CartItemErrorCode.NOT_FOUND));

        CartItem cartItem = CartItem.builder()
                .quantity(cartItemAddRequestDTO.getQuantity())
                .price(cartItemAddRequestDTO.getPrice())
                .build();

        cartItem.setCart(cart);
        cartItem.setMenu(menu);
        cartItem.setMenuOption(menuOption);

        cartItemRepository.save(cartItem);

        return CartItemConverter.toCartItemAddResponseDTO(cartItem);
    }

    public CartItemResponseDTO.CartItemUpdateResponseDTO updateCartItem(CartItemRequestDTO.CartItemUpdateRequestDTO cartItemUpdateRequestDTO, UUID cartItemId, User user) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(()->new CartItemException(CartItemErrorCode.NOT_FOUND));
        if(!cartItemRepository.existsByCartItemAndUser(cartItemId,user.getId())){
            throw new CartItemException(CartItemErrorCode.UNAUTHORIZED_ACCESS);
        }
        Menu menu = menuRepository.findById(cartItemUpdateRequestDTO.getMenuId()).orElseThrow(()->new CartItemException(CartItemErrorCode.NOT_FOUND));
        MenuOption menuOption = menuOptionRepository.findById(cartItemUpdateRequestDTO.getMenuOptionId()).orElseThrow(()->new CartItemException(CartItemErrorCode.NOT_FOUND));
        cartItem.update(cartItemUpdateRequestDTO);
        cartItem.setMenu(menu);
        cartItem.setMenuOption(menuOption);
        cartItemRepository.save(cartItem);
        return CartItemConverter.toCartItemUpdateResponseDTO(cartItem,menu.getId(),menuOption.getId());
    }

    public void deleteCartItem(UUID cartItemId, User user) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(()->new CartItemException(CartItemErrorCode.NOT_FOUND));
        if(!cartItemRepository.existsByCartItemAndUser(cartItemId,user.getId())){
            throw new CartItemException(CartItemErrorCode.UNAUTHORIZED_ACCESS);
        }
        cartItemRepository.delete(cartItem);
    }
}
