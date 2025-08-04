package com.example.cloudfour.peopleofdelivery.unit.domain.cart.service.query;

import com.example.cloudfour.peopleofdelivery.domain.cart.dto.CartResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.cart.entity.Cart;
import com.example.cloudfour.peopleofdelivery.domain.cart.exception.CartErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.cart.exception.CartException;
import com.example.cloudfour.peopleofdelivery.domain.cart.repository.CartRepository;
import com.example.cloudfour.peopleofdelivery.domain.cart.service.query.CartQueryService;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.global.auth.userdetails.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CartQueryServiceTest {

    @InjectMocks
    private CartQueryService cartQueryService;

    @Mock
    private CartRepository cartRepository;

    @Test
    @DisplayName("장바구니 조회 성공")
    void getCartListById_success() {
        UUID cartId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();

        User mockUser = createMockUser(userId);
        CustomUserDetails mockUserDetails = createMockCustomUserDetails(userId);
        Store mockStore = createMockStore(storeId);
        Cart mockCart = createMockCart(cartId, mockUser, mockStore);

        when(cartRepository.findByIdAndUser(cartId, userId)).thenReturn(Optional.of(mockCart));

        CartResponseDTO.CartDetailResponseDTO response = cartQueryService.getCartListById(cartId, mockUserDetails);

        assertThat(response).isNotNull();
        assertThat(response.getCartId()).isEqualTo(cartId);
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getStoreId()).isEqualTo(storeId);

        verify(cartRepository).findByIdAndUser(cartId, userId);
    }

    @Test
    @DisplayName("장바구니 조회 실패 - 존재하지 않는 장바구니")
    void getCartListById_fail_cartNotFound() {
        UUID cartId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        CustomUserDetails mockUserDetails = createMockCustomUserDetails(userId);

        when(cartRepository.findByIdAndUser(cartId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartQueryService.getCartListById(cartId, mockUserDetails))
                .isInstanceOf(CartException.class)
                .hasFieldOrPropertyWithValue("code", CartErrorCode.NOT_FOUND);

        verify(cartRepository).findByIdAndUser(cartId, userId);
    }

    private CustomUserDetails createMockCustomUserDetails(UUID userId) {
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(userId);
        return userDetails;
    }

    private User createMockUser(UUID userId) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        return user;
    }

    private Store createMockStore(UUID storeId) {
        Store store = mock(Store.class);
        when(store.getId()).thenReturn(storeId);
        return store;
    }

    private Cart createMockCart(UUID cartId, User user, Store store) {
        Cart cart = mock(Cart.class);
        when(cart.getId()).thenReturn(cartId);
        when(cart.getUser()).thenReturn(user);
        when(cart.getStore()).thenReturn(store);
        return cart;
    }
}