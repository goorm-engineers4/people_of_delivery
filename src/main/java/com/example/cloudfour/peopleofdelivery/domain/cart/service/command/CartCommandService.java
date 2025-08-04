package com.example.cloudfour.peopleofdelivery.domain.cart.service.command;

import com.example.cloudfour.peopleofdelivery.domain.cart.converter.CartConverter;
import com.example.cloudfour.peopleofdelivery.domain.cart.dto.CartRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.cart.dto.CartResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.cart.entity.Cart;
import com.example.cloudfour.peopleofdelivery.domain.cart.exception.CartErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.cart.exception.CartException;
import com.example.cloudfour.peopleofdelivery.domain.cart.repository.CartRepository;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.converter.CartItemConverter;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.dto.CartItemRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.dto.CartItemResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.service.command.CartItemCommandService;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.Menu;
import com.example.cloudfour.peopleofdelivery.domain.menu.exception.MenuErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.menu.exception.MenuException;
import com.example.cloudfour.peopleofdelivery.domain.menu.repository.MenuRepository;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.domain.user.exception.UserErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.user.exception.UserException;
import com.example.cloudfour.peopleofdelivery.domain.user.repository.UserRepository;
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
public class CartCommandService {
    private final CartRepository cartRepository;
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final CartItemCommandService cartItemCommandService;

    public CartResponseDTO.CartCreateResponseDTO createCart(CartRequestDTO.CartCreateRequestDTO cartCreateRequestDTO, CustomUserDetails user) {
        Store store = storeRepository.findById(cartCreateRequestDTO.getStoreId())
                .orElseThrow(() -> new CartException(CartErrorCode.NOT_FOUND));
        User finduser = userRepository.findById(user.getId()).orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));
        boolean exists = cartRepository.existsByUserAndStore(user.getId(), store.getId());
        if (exists) {
            throw new CartException(CartErrorCode.ALREADY_ADD);
        }
        log.info("권한 확인");
        Cart cart = Cart.builder()
                .build();
        cart.setUser(finduser);
        cart.setStore(store);
        Cart savedCart = cartRepository.save(cart);
        log.info("장바구니 생성 완료, cartId={}", savedCart.getId());

        Menu menu = menuRepository.findById(cartCreateRequestDTO.getMenuId()).orElseThrow(()->new MenuException(MenuErrorCode.NOT_FOUND));
        CartItemRequestDTO.CartItemAddRequestDTO cartItemAddRequestDTO = CartItemConverter.toCartItemAddRequestDTO(cartCreateRequestDTO,menu.getPrice());
        CartItemResponseDTO.CartItemAddResponseDTO cartItemAddResponseDTO = cartItemCommandService.AddCartItem(cartItemAddRequestDTO, savedCart.getId(), user);
        return CartConverter.toCartCreateResponseDTO(savedCart,cartItemAddResponseDTO.getCartItemId());

    }

    public void deleteCart(UUID cartId, CustomUserDetails user) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartException(CartErrorCode.NOT_FOUND));

        if (!cart.getUser().getId().equals(user.getId())) {
            throw new CartException(CartErrorCode.UNAUTHORIZED_ACCESS);
        }
        cartRepository.delete(cart);
    }
}
