package com.example.cloudfour.peopleofdelivery.unit.domain.cart.service.command;

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
import com.example.cloudfour.peopleofdelivery.domain.menu.repository.MenuRepository;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.domain.user.repository.UserRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("장바구니 생성 성공")
    void createCart_success() {
        UUID storeId = UUID.randomUUID();
        UUID menuId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        UUID cartItemId = UUID.randomUUID();
        Integer menuPrice = 15000;

        User mockUserEntity = createMockUser(userId);
        CustomUserDetails mockUserDetails = createMockCustomUserDetails(userId);
        Store mockStore = createMockStore(storeId);
        Menu mockMenu = createMockMenu(menuId, menuPrice);
        Cart mockCart = createMockCart(cartId, mockUserEntity, mockStore);

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
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUserEntity));
        when(cartRepository.existsByUserAndStore(userId, storeId)).thenReturn(false);
        when(cartRepository.save(any(Cart.class))).thenReturn(mockCart);
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(mockMenu));
        when(cartItemCommandService.AddCartItem(any(CartItemRequestDTO.CartItemAddRequestDTO.class), eq(cartId), eq(mockUserDetails)))
                .thenReturn(mockCartItemResponse);

        CartResponseDTO.CartCreateResponseDTO response = cartCommandService.createCart(request, mockUserDetails);

        assertThat(response).isNotNull();
        verify(storeRepository).findById(storeId);
        verify(cartRepository).existsByUserAndStore(userId, storeId);
        verify(cartRepository).save(any(Cart.class));
        verify(menuRepository).findById(menuId);
        verify(cartItemCommandService).AddCartItem(any(CartItemRequestDTO.CartItemAddRequestDTO.class), eq(cartId), eq(mockUserDetails));
    }

    @Test
    @DisplayName("장바구니 생성 실패 - 존재하지 않는 가게")
    void createCart_fail_store_not_found() {
        UUID storeId = UUID.randomUUID();
        UUID menuId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        CustomUserDetails mockUserDetails = createMockCustomUserDetails(userId);

        CartRequestDTO.CartCreateRequestDTO request = CartRequestDTO.CartCreateRequestDTO.builder()
                .storeId(storeId)
                .menuId(menuId)
                .menuOptionId(UUID.randomUUID())
                .build();

        when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartCommandService.createCart(request, mockUserDetails))
                .isInstanceOf(CartException.class)
                .hasMessageContaining(CartErrorCode.NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("장바구니 생성 실패 - 이미 해당 가게의 장바구니 존재")
    void createCart_fail_cart_already_exists() {
        UUID storeId = UUID.randomUUID();
        UUID menuId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User mockUserEntity = createMockUser(userId);
        CustomUserDetails mockUserDetails = createMockCustomUserDetails(userId);
        Store mockStore = createMockStore(storeId);

        CartRequestDTO.CartCreateRequestDTO request = CartRequestDTO.CartCreateRequestDTO.builder()
                .storeId(storeId)
                .menuId(menuId)
                .menuOptionId(UUID.randomUUID())
                .build();

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(mockStore));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUserEntity));
        when(cartRepository.existsByUserAndStore(userId, storeId)).thenReturn(true);

        assertThatThrownBy(() -> cartCommandService.createCart(request, mockUserDetails))
                .isInstanceOf(CartException.class)
                .hasMessageContaining(CartErrorCode.ALREADY_ADD.getMessage());
    }


    @Test
    @DisplayName("장바구니 삭제 성공")
    void deleteCart_success() {
        UUID cartId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User mockUser = createMockUser(userId);
        Cart mockCart = createMockCart(cartId, mockUser, null);
        CustomUserDetails mockUserDetails = createMockCustomUserDetails(userId);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(mockCart));

        cartCommandService.deleteCart(cartId, mockUserDetails);

        verify(cartRepository).delete(mockCart);
    }

    @Test
    @DisplayName("장바구니 삭제 실패 - 존재하지 않는 장바구니")
    void deleteCart_fail_cart_not_found() {
        UUID cartId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        CustomUserDetails mockUserDetails = createMockCustomUserDetails(userId);

        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartCommandService.deleteCart(cartId, mockUserDetails))
                .isInstanceOf(CartException.class)
                .hasMessageContaining(CartErrorCode.NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("장바구니 삭제 실패 - 권한 없는 사용자")
    void deleteCart_fail_unauthorized() {
        UUID cartId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        
        User ownerUser = createMockUser(ownerId);
        Cart mockCart = createMockCart(cartId, ownerUser, null);
        CustomUserDetails requesterUserDetails = createMockCustomUserDetails(requesterId);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(mockCart));

        assertThatThrownBy(() -> cartCommandService.deleteCart(cartId, requesterUserDetails))
                .isInstanceOf(CartException.class)
                .hasMessageContaining(CartErrorCode.UNAUTHORIZED_ACCESS.getMessage());
    }

    private User createMockUser(UUID userId) {
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);
        return mockUser;
    }

    private Store createMockStore(UUID storeId) {
        Store mockStore = mock(Store.class);
        when(mockStore.getId()).thenReturn(storeId);
        return mockStore;
    }

    private Menu createMockMenu(UUID menuId, Integer price) {
        Menu mockMenu = mock(Menu.class);
        when(mockMenu.getId()).thenReturn(menuId);
        when(mockMenu.getPrice()).thenReturn(price);
        return mockMenu;
    }

    private Cart createMockCart(UUID cartId, User user, Store store) {
        Cart mockCart = mock(Cart.class);
        when(mockCart.getId()).thenReturn(cartId);
        when(mockCart.getUser()).thenReturn(user);
        when(mockCart.getStore()).thenReturn(store);
        return mockCart;
    }

    private CustomUserDetails createMockCustomUserDetails(UUID userId) {
        CustomUserDetails mockUserDetails = mock(CustomUserDetails.class);
        when(mockUserDetails.getId()).thenReturn(userId);
        return mockUserDetails;
    }
}