package com.example.cloudfour.peopleofdelivery.domain.cart.service.command;

import com.example.cloudfour.peopleofdelivery.domain.cart.converter.CartConverter;
import com.example.cloudfour.peopleofdelivery.domain.cart.dto.CartRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.cart.dto.CartResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.cart.entity.Cart;
import com.example.cloudfour.peopleofdelivery.domain.cart.exception.CartErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.cart.exception.CartException;
import com.example.cloudfour.peopleofdelivery.domain.cart.repository.CartRepository;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreRepository;
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
public class CartCommandService {
    CartRepository cartRepository;
    StoreRepository storeRepository;

    public CartResponseDTO.CartCreateResponseDTO createCart(CartRequestDTO.CartCreateRequestDTO cartCreateRequestDTO, User user) {
        Store store = storeRepository.findById(cartCreateRequestDTO.getStoreId())
                .orElseThrow(() -> new CartException(CartErrorCode.NOT_FOUND));

        boolean exists = cartRepository.existsByUserAndStore(user.getId(), store.getId());
        if (exists) {
            throw new CartException(CartErrorCode.ALREADY_ADD);
        }

        Cart cart = Cart.builder()
                .user(user)
                .store(store)
                .build();

        Cart savedCart = cartRepository.save(cart);
        log.info("장바구니 생성 완료, cartId={}", savedCart.getId());

        return CartConverter.toCartCreateResponseDTO(savedCart);

    }

    public void deleteCart(UUID cartId, User user) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartException(CartErrorCode.NOT_FOUND));

        // 권한 체크 (Cart 소유자와 요청 User 비교)
        if (!cart.getUser().getId().equals(user.getId())) {
            throw new CartException(CartErrorCode.UNAUTHORIZED_ACCESS);
        }
        cartRepository.delete(cart);
    }
}
