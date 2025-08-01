package com.example.cloudfour.peopleofdelivery.unit.domain.cart.service.command.command;

import com.example.cloudfour.peopleofdelivery.domain.cart.dto.CartRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.cart.dto.CartResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.cart.entity.Cart;
import com.example.cloudfour.peopleofdelivery.domain.cart.exception.CartErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.cart.exception.CartException;
import com.example.cloudfour.peopleofdelivery.domain.cart.repository.CartRepository;
import com.example.cloudfour.peopleofdelivery.domain.cart.service.command.CartCommandService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CartCommandServiceTest {

    @InjectMocks
    private CartCommandService cartCommandService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private CartItemCommandService cartItemCommandService;

    @Test
    @DisplayName("장바구니 생성 성공")
    void createCart_success() {
        UUID storeId = UUID.randomUUID();
        UUID menuId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        UUID cartItemId = UUID.randomUUID();
        Integer menuPrice = 15000;

        User mockUser = createMockUser(userId);
        Store mockStore = createMockStore(storeId);
        Menu mockMenu = createMockMenu(menuId, menuPrice);
        Cart mockCart = createMockCart(cartId, mockUser, mockStore);

        CartRequestDTO.CartCreateRequestDTO request = CartRequestDTO.CartCreateRequestDTO.builder()
                .storeId(storeId)
                .menuId(menuId)
                .menuOptionId(UUID.randomUUID())
                .build();

        CartItemResponseDTO.CartItemAddResponseDTO mockCartItemResponse = 
                CartItemResponseDTO.CartItemAddResponseDTO.builder()
                        .cartItemId(cartItemId)
                        .build();

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(mockStore));
        when(cartRepository.existsByUserAndStore(userId, storeId)).thenReturn(false);
        when(cartRepository.save(any(Cart.class))).thenReturn(mockCart);
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(mockMenu));
        when(cartItemCommandService.AddCartItem(any(CartItemRequestDTO.CartItemAddRequestDTO.class), eq(cartId), eq(mockUser)))
                .thenReturn(mockCartItemResponse);

        CartResponseDTO.CartCreateResponseDTO response = cartCommandService.createCart(request, mockUser);

        assertThat(response).isNotNull();
        verify(storeRepository).findById(storeId);
        verify(cartRepository).existsByUserAndStore(userId, storeId);
        verify(cartRepository).save(any(Cart.class));
        verify(menuRepository).findById(menuId);
        verify(cartItemCommandService).AddCartItem(any(CartItemRequestDTO.CartItemAddRequestDTO.class), eq(cartId), eq(mockUser));
    }

    @Test
    @DisplayName("장바구니 생성 실패 - 존재하지 않는 가게")
    void createCart_fail_store_not_found() {
        UUID storeId = UUID.randomUUID();
        UUID menuId = UUID.randomUUID();
        User mockUser = createMockUser(UUID.randomUUID());

        CartRequestDTO.CartCreateRequestDTO request = CartRequestDTO.CartCreateRequestDTO.builder()
                .storeId(storeId)
                .menuId(menuId)
                .menuOptionId(UUID.randomUUID())
                .build();

        when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartCommandService.createCart(request, mockUser))
                .isInstanceOf(CartException.class)
                .hasFieldOrPropertyWithValue("code", CartErrorCode.NOT_FOUND);

        verify(storeRepository).findById(storeId);
        verify(cartRepository, never()).existsByUserAndStore(any(), any());
        verify(cartRepository, never()).save(any());
    }

    @Test
    @DisplayName("장바구니 생성 실패 - 이미 해당 가게의 장바구니 존재")
    void createCart_fail_cart_already_exists() {
        UUID storeId = UUID.randomUUID();
        UUID menuId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        
        User mockUser = createMockUser(userId);
        Store mockStore = createMockStore(storeId);

        CartRequestDTO.CartCreateRequestDTO request = CartRequestDTO.CartCreateRequestDTO.builder()
                .storeId(storeId)
                .menuId(menuId)
                .menuOptionId(UUID.randomUUID())
                .build();

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(mockStore));
        when(cartRepository.existsByUserAndStore(userId, storeId)).thenReturn(true);

        assertThatThrownBy(() -> cartCommandService.createCart(request, mockUser))
                .isInstanceOf(CartException.class)
                .hasFieldOrPropertyWithValue("code", CartErrorCode.ALREADY_ADD);

        verify(storeRepository).findById(storeId);
        verify(cartRepository).existsByUserAndStore(userId, storeId);
        verify(cartRepository, never()).save(any());
    }

    @Test
    @DisplayName("장바구니 생성 실패 - 존재하지 않는 메뉴")
    void createCart_fail_menu_not_found() {
        UUID storeId = UUID.randomUUID();
        UUID menuId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();

        User mockUser = createMockUser(userId);
        Store mockStore = createMockStore(storeId);
        Cart mockCart = createMockCart(cartId, mockUser, mockStore);

        CartRequestDTO.CartCreateRequestDTO request = CartRequestDTO.CartCreateRequestDTO.builder()
                .storeId(storeId)
                .menuId(menuId)
                .menuOptionId(UUID.randomUUID())
                .build();

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(mockStore));
        when(cartRepository.existsByUserAndStore(userId, storeId)).thenReturn(false);
        when(cartRepository.save(any(Cart.class))).thenReturn(mockCart);
        when(menuRepository.findById(menuId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartCommandService.createCart(request, mockUser))
                .isInstanceOf(MenuException.class)
                .hasFieldOrPropertyWithValue("code", MenuErrorCode.NOT_FOUND);

        verify(storeRepository).findById(storeId);
        verify(cartRepository).existsByUserAndStore(userId, storeId);
        verify(cartRepository).save(any(Cart.class));
        verify(menuRepository).findById(menuId);
        verify(cartItemCommandService, never()).AddCartItem(any(), any(), any());
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

    private Menu createMockMenu(UUID menuId, Integer price) {
        Menu menu = mock(Menu.class);
        when(menu.getId()).thenReturn(menuId);
        lenient().when(menu.getPrice()).thenReturn(price); // lenient() 사용
        return menu;
    }

    private Cart createMockCart(UUID cartId, User user, Store store) {
        Cart cart = mock(Cart.class);
        when(cart.getId()).thenReturn(cartId);
        when(cart.getUser()).thenReturn(user);
        when(cart.getStore()).thenReturn(store);
        return cart;
    }
}