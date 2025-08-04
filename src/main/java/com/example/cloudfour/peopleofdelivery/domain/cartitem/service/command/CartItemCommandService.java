package com.example.cloudfour.peopleofdelivery.domain.cartitem.service.command;

import com.example.cloudfour.peopleofdelivery.domain.cart.entity.Cart;
import com.example.cloudfour.peopleofdelivery.domain.cart.exception.CartErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.cart.exception.CartException;
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
import com.example.cloudfour.peopleofdelivery.global.auth.userdetails.CustomUserDetails;
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
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final MenuRepository menuRepository;
    private final MenuOptionRepository menuOptionRepository;

    public CartItemResponseDTO.CartItemAddResponseDTO AddCartItem(CartItemRequestDTO.CartItemAddRequestDTO cartItemAddRequestDTO, UUID cartId, CustomUserDetails user) {
        Cart cart = cartRepository.findByIdAndUser(cartId, user.getId())
                .orElseThrow(() -> new CartItemException(CartItemErrorCode.NOT_FOUND));
        Menu menu = menuRepository.findById(cartItemAddRequestDTO.getMenuId())
                .orElseThrow(() -> new CartItemException(CartItemErrorCode.NOT_FOUND));
        MenuOption menuOption = menuOptionRepository.findById(cartItemAddRequestDTO.getMenuOptionId())
                .orElseThrow(() -> new CartItemException(CartItemErrorCode.NOT_FOUND));

        CartItem cartItem = CartItem.builder()
                .quantity(cartItemAddRequestDTO.getQuantity())
                .price(cartItemAddRequestDTO.getPrice()+menuOption.getAdditionalPrice())
                .build();

        cartItem.setCart(cart);
        cartItem.setMenu(menu);
        cartItem.setMenuOption(menuOption);

        cartItemRepository.save(cartItem);

        return CartItemConverter.toCartItemAddResponseDTO(cartItem);
    }

    public CartItemResponseDTO.CartItemAddResponseDTO CreateCartItem(CartItemRequestDTO.CartItemCreateRequestDTO cartItemCreateRequestDTO, UUID cartId, CustomUserDetails user) {
        Cart cart = cartRepository.findByIdAndUser(cartId, user.getId())
                .orElseThrow(() -> new CartItemException(CartItemErrorCode.NOT_FOUND));
        Menu menu = menuRepository.findById(cartItemCreateRequestDTO.getMenuId())
                .orElseThrow(() -> new CartItemException(CartItemErrorCode.NOT_FOUND));
        MenuOption menuOption = menuOptionRepository.findById(cartItemCreateRequestDTO.getMenuOptionId())
                .orElseThrow(() -> new CartItemException(CartItemErrorCode.NOT_FOUND));

        CartItem cartItem = CartItem.builder()
                .quantity(1)
                .price(menu.getPrice()+menuOption.getAdditionalPrice())
                .build();

        cartItem.setCart(cart);
        cartItem.setMenu(menu);
        cartItem.setMenuOption(menuOption);

        cartItemRepository.save(cartItem);

        return CartItemConverter.toCartItemAddResponseDTO(cartItem);
    }

    public CartItemResponseDTO.CartItemUpdateResponseDTO updateCartItem(CartItemRequestDTO.CartItemUpdateRequestDTO cartItemUpdateRequestDTO, UUID cartItemId, CustomUserDetails user) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(()->new CartItemException(CartItemErrorCode.NOT_FOUND));
        if(!cartItemRepository.existsByCartItemAndUser(cartItemId,user.getId())){
            throw new CartItemException(CartItemErrorCode.UNAUTHORIZED_ACCESS);
        }
        MenuOption menuOption = menuOptionRepository.findById(cartItemUpdateRequestDTO.getMenuOptionId()).orElseThrow(()->new CartItemException(CartItemErrorCode.NOT_FOUND));
        Integer quantity = cartItemUpdateRequestDTO.getQuantity();
        if(quantity <=0){
            throw new CartItemException(CartItemErrorCode.UPDATE_FAILED);
        }
        cartItem.update(quantity, quantity*(cartItem.getMenu().getPrice()+menuOption.getAdditionalPrice()));
        cartItem.setMenuOption(menuOption);
        cartItemRepository.save(cartItem);
        return CartItemConverter.toCartItemUpdateResponseDTO(cartItem);
    }

    public void deleteCartItem(UUID cartItemId, CustomUserDetails user) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(()->new CartItemException(CartItemErrorCode.NOT_FOUND));
        Cart cart = cartRepository.findById(cartItem.getCart().getId()).orElseThrow(()->new CartException(CartErrorCode.NOT_FOUND));
        if(!cartItemRepository.existsByCartItemAndUser(cartItemId,user.getId())){
            throw new CartItemException(CartItemErrorCode.UNAUTHORIZED_ACCESS);
        }
        cartItemRepository.delete(cartItem);
        cartItemRepository.flush();
        if(cart.getCartItems().isEmpty()){
            cartRepository.delete(cart);
        }
    }
}
