package com.example.cloudfour.peopleofdelivery.unit.domain.cartitem.service.query;

import com.example.cloudfour.peopleofdelivery.domain.cart.entity.Cart;
import com.example.cloudfour.peopleofdelivery.domain.cart.repository.CartRepository;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.dto.CartItemResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.entity.CartItem;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.exception.CartItemErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.exception.CartItemException;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.repository.CartItemRepository;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.service.query.CartItemQueryService;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.Menu;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuOption;
import com.example.cloudfour.peopleofdelivery.global.auth.userdetails.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CartItemQueryServiceTest {

    @InjectMocks
    private CartItemQueryService cartItemQueryService;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartRepository cartRepository;

    @Test
    @DisplayName("장바구니 항목 단일 조회 성공")
    void getCartItemById_success() {
        UUID cartItemId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        CustomUserDetails mockUserDetails = createMockCustomUserDetails(userId);
        CartItem mockCartItem = createMockCartItem(cartItemId, cartId);

        when(cartItemRepository.existsByCartItemAndUser(cartItemId, userId)).thenReturn(true);
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(mockCartItem));

        CartItemResponseDTO.CartItemListResponseDTO response = cartItemQueryService.getCartItemById(cartItemId, mockUserDetails);

        assertThat(response).isNotNull();
        assertThat(response.getCartItemId()).isEqualTo(cartItemId);
        assertThat(response.getCartId()).isEqualTo(cartId);

        verify(cartItemRepository).existsByCartItemAndUser(cartItemId, userId);
        verify(cartItemRepository).findById(cartItemId);
    }

    @Test
    @DisplayName("장바구니 항목 단일 조회 실패 - 권한 없음")
    void getCartItemById_fail_unauthorized() {
        UUID cartItemId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        CustomUserDetails mockUserDetails = createMockCustomUserDetails(userId);

        when(cartItemRepository.existsByCartItemAndUser(cartItemId, userId)).thenReturn(false);

        assertThatThrownBy(() -> cartItemQueryService.getCartItemById(cartItemId, mockUserDetails))
                .isInstanceOf(CartItemException.class)
                .hasFieldOrPropertyWithValue("code", CartItemErrorCode.UNAUTHORIZED_ACCESS);

        verify(cartItemRepository).existsByCartItemAndUser(cartItemId, userId);
    }

    @Test
    @DisplayName("장바구니 항목 단일 조회 실패 - 항목 없음")
    void getCartItemById_fail_notFound() {
        UUID cartItemId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        CustomUserDetails mockUserDetails = createMockCustomUserDetails(userId);

        when(cartItemRepository.existsByCartItemAndUser(cartItemId, userId)).thenReturn(true);
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartItemQueryService.getCartItemById(cartItemId, mockUserDetails))
                .isInstanceOf(CartItemException.class)
                .hasFieldOrPropertyWithValue("code", CartItemErrorCode.NOT_FOUND);

        verify(cartItemRepository).existsByCartItemAndUser(cartItemId, userId);
        verify(cartItemRepository).findById(cartItemId);
    }

    @Test
    @DisplayName("장바구니 항목 리스트 조회 성공")
    void getCartItemList_success() {
        UUID cartId = UUID.randomUUID();
        UUID cartItemId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        CustomUserDetails mockUserDetails = createMockCustomUserDetails(userId);
        CartItem mockCartItem = createMockCartItem(cartItemId, cartId);

        when(cartRepository.existsByUserAndCart(userId, cartId)).thenReturn(true);
        when(cartItemRepository.findAllByCartId(cartId, userId)).thenReturn(List.of(mockCartItem));

        List<CartItemResponseDTO.CartItemListResponseDTO> responseList =
                cartItemQueryService.getCartItemList(cartId, mockUserDetails);

        assertThat(responseList).isNotNull();
        assertThat(responseList.size()).isEqualTo(1);
        assertThat(responseList.get(0).getCartItemId()).isEqualTo(cartItemId);
        assertThat(responseList.get(0).getCartId()).isEqualTo(cartId);

        verify(cartRepository).existsByUserAndCart(userId, cartId);
        verify(cartItemRepository).findAllByCartId(cartId, userId);
    }

    @Test
    @DisplayName("장바구니 항목 리스트 조회 실패 - 권한 없음")
    void getCartItemList_fail_unauthorized() {
        UUID cartId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        CustomUserDetails mockUserDetails = createMockCustomUserDetails(userId);

        when(cartRepository.existsByUserAndCart(userId, cartId)).thenReturn(false);

        assertThatThrownBy(() -> cartItemQueryService.getCartItemList(cartId, mockUserDetails))
                .isInstanceOf(CartItemException.class)
                .hasFieldOrPropertyWithValue("code", CartItemErrorCode.UNAUTHORIZED_ACCESS);

        verify(cartRepository).existsByUserAndCart(userId, cartId);
    }

    @Test
    @DisplayName("장바구니 항목 리스트 조회 실패 - 항목 없음")
    void getCartItemList_fail_notFound() {
        UUID cartId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        CustomUserDetails mockUserDetails = createMockCustomUserDetails(userId);

        when(cartRepository.existsByUserAndCart(userId, cartId)).thenReturn(true);
        when(cartItemRepository.findAllByCartId(cartId, userId)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> cartItemQueryService.getCartItemList(cartId, mockUserDetails))
                .isInstanceOf(CartItemException.class)
                .hasFieldOrPropertyWithValue("code", CartItemErrorCode.NOT_FOUND);

        verify(cartRepository).existsByUserAndCart(userId, cartId);
        verify(cartItemRepository).findAllByCartId(cartId, userId);
    }


    private CustomUserDetails createMockCustomUserDetails(UUID userId) {
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(userId);
        return userDetails;
    }

    private CartItem createMockCartItem(UUID cartItemId, UUID cartId) {
        Cart cart = mock(Cart.class);
        when(cart.getId()).thenReturn(cartId);

        UUID menuId = UUID.randomUUID();
        UUID menuOptionId = UUID.randomUUID();

        Menu menu = mock(Menu.class);
        when(menu.getId()).thenReturn(menuId);

        MenuOption menuOption = mock(MenuOption.class);
        when(menuOption.getId()).thenReturn(menuOptionId);

        CartItem cartItem = mock(CartItem.class);
        when(cartItem.getId()).thenReturn(cartItemId);
        when(cartItem.getCart()).thenReturn(cart);
        when(cartItem.getMenu()).thenReturn(menu);
        when(cartItem.getMenuOption()).thenReturn(menuOption);
        when(cartItem.getQuantity()).thenReturn(1);
        when(cartItem.getPrice()).thenReturn(1000);

        return cartItem;
    }

}
