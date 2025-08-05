package com.example.cloudfour.peopleofdelivery.unit.domain.cartitem.service.command;

import com.example.cloudfour.peopleofdelivery.domain.cart.entity.Cart;
import com.example.cloudfour.peopleofdelivery.domain.cart.repository.CartRepository;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.dto.CartItemRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.dto.CartItemResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.entity.CartItem;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.exception.CartItemException;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.repository.CartItemRepository;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.service.command.CartItemCommandService;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.Menu;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuOption;
import com.example.cloudfour.peopleofdelivery.domain.menu.repository.MenuOptionRepository;
import com.example.cloudfour.peopleofdelivery.domain.menu.repository.MenuRepository;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CartItemCommandServiceTest {

    @InjectMocks
    private CartItemCommandService cartItemCommandService;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuOptionRepository menuOptionRepository;

    @Mock
    private UserRepository userRepository;

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

    private Cart createMockCart(UUID cartId, User user) {
        Cart mockCart = mock(Cart.class);
        when(mockCart.getId()).thenReturn(cartId);
        when(mockCart.getUser()).thenReturn(user);
        return mockCart;
    }

    private Menu createMockMenu(UUID menuId, Integer price) {
        Menu mockMenu = mock(Menu.class);
        when(mockMenu.getId()).thenReturn(menuId);
        when(mockMenu.getPrice()).thenReturn(price);
        return mockMenu;
    }
    private MenuOption createMockMenuOption(UUID menuOptionId, Menu menu, int additionalPrice) {
        MenuOption menuOption = mock(MenuOption.class);
        given(menuOption.getId()).willReturn(menuOptionId);
        given(menuOption.getMenu()).willReturn(menu);
        given(menuOption.getAdditionalPrice()).willReturn(additionalPrice);
        return menuOption;
    }

    private CartItem createMockCartItem(UUID cartItemId, Cart cart, Menu menu, MenuOption menuOption, int quantity, int price) {
        CartItem cartItem = mock(CartItem.class);
        given(cartItem.getId()).willReturn(cartItemId);
        given(cartItem.getCart()).willReturn(cart);
        given(cartItem.getMenu()).willReturn(menu);
        given(cartItem.getMenuOption()).willReturn(menuOption);
        given(cartItem.getQuantity()).willReturn(quantity);
        given(cartItem.getPrice()).willReturn(price);
        return cartItem;
    }

    private CustomUserDetails createMockCustomUserDetails(UUID userId) {
        CustomUserDetails mockUserDetails = mock(CustomUserDetails.class);
        when(mockUserDetails.getId()).thenReturn(userId);
        return mockUserDetails;
    }

    @Test
    @DisplayName("장바구니 항목 추가 성공")
    void addCartItem_success() {
        UUID userId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        UUID menuId = UUID.randomUUID();
        UUID menuOptionId = UUID.randomUUID();
        UUID cartItemId = UUID.randomUUID();
        Integer menuPrice = 10000;

        User mockUserEntity = createMockUser(userId);
        CustomUserDetails mockUserDetails = createMockCustomUserDetails(userId);
        Cart mockCart = createMockCart(cartId, mockUserEntity);
        Menu mockMenu = createMockMenu(menuId, menuPrice);
        MenuOption mockMenuOption = createMockMenuOption(menuOptionId, mockMenu, 2000);
        CartItem mockCartItem = createMockCartItem(cartItemId, mockCart, mockMenu, mockMenuOption, 1, 12000);

        CartItemRequestDTO.CartItemAddRequestDTO request = CartItemRequestDTO.CartItemAddRequestDTO.builder()
                .menuId(menuId)
                .menuOptionId(menuOptionId)
                .quantity(1)
                .price(menuPrice)
                .build();

        given(cartRepository.findByIdAndUser(cartId, userId)).willReturn(Optional.of(mockCart));
        given(menuRepository.findById(menuId)).willReturn(Optional.of(mockMenu));
        given(menuOptionRepository.findById(menuOptionId)).willReturn(Optional.of(mockMenuOption));
        given(cartItemRepository.save(any(CartItem.class))).willAnswer(invocation -> {
            CartItem saved = invocation.getArgument(0);
            java.lang.reflect.Field idField = CartItem.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(saved, cartItemId);
            return saved;
        });

        CartItemResponseDTO.CartItemAddResponseDTO response = cartItemCommandService.AddCartItem(request, cartId, mockUserDetails);

        assertThat(response).isNotNull();
        assertThat(response.getCartItemId()).isEqualTo(cartItemId);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }


    @Test
    @DisplayName("장바구니 항목 추가 실패 - 장바구니를 찾을 수 없음")
    void addCartItem_fail_cartNotFound() {
        UUID userId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        CustomUserDetails mockUserDetails = createMockCustomUserDetails(userId);
        CartItemRequestDTO.CartItemAddRequestDTO request = CartItemRequestDTO.CartItemAddRequestDTO.builder()
                .menuId(UUID.randomUUID())
                .menuOptionId(UUID.randomUUID())
                .quantity(1)
                .price(10000)
                .build();

        given(cartRepository.findByIdAndUser(cartId, userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> cartItemCommandService.AddCartItem(request, cartId, mockUserDetails))
                .isInstanceOf(CartItemException.class);
    }

    @Test
    @DisplayName("장바구니 항목 추가 실패 - 메뉴를 찾을 수 없음")
    void addCartItem_fail_menuNotFound() {
        UUID userId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        UUID menuId = UUID.randomUUID();
        CustomUserDetails mockUserDetails = createMockCustomUserDetails(userId);
        Cart mockCart = createMockCart(cartId, null);
        CartItemRequestDTO.CartItemAddRequestDTO request = CartItemRequestDTO.CartItemAddRequestDTO.builder()
                .menuId(menuId)
                .build();

        given(cartRepository.findByIdAndUser(cartId, userId)).willReturn(Optional.of(mockCart));
        given(menuRepository.findById(menuId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> cartItemCommandService.AddCartItem(request, cartId, mockUserDetails))
                .isInstanceOf(CartItemException.class);
    }

    @Test
    @DisplayName("장바구니 항목 수정 성공")
    void updateCartItem_success() {
        UUID userId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        UUID menuId = UUID.randomUUID();
        UUID menuOptionId = UUID.randomUUID();
        UUID cartItemId = UUID.randomUUID();

        CustomUserDetails mockUserDetails = createMockCustomUserDetails(userId);
        User mockUser = createMockUser(userId);
        Cart mockCart = createMockCart(cartId, mockUser);
        Menu mockMenu = createMockMenu(menuId, 5000);
        MenuOption mockMenuOption = createMockMenuOption(menuOptionId, mockMenu, 0);
        CartItem mockCartItem = createMockCartItem(cartItemId, mockCart, mockMenu, mockMenuOption, 1, 5000);
        CartItemRequestDTO.CartItemUpdateRequestDTO request = CartItemRequestDTO.CartItemUpdateRequestDTO.builder()
                .quantity(2)
                .menuOptionId(menuOptionId)
                .build();

        given(cartItemRepository.findById(cartItemId)).willReturn(Optional.of(mockCartItem));
        given(menuOptionRepository.findById(menuOptionId)).willReturn(Optional.of(mockMenuOption));
        given(cartItemRepository.existsByCartItemAndUser(cartItemId, userId)).willReturn(true);

        cartItemCommandService.updateCartItem(request, cartItemId, mockUserDetails);

        verify(mockCartItem).update(2, 10000);
    }
    
    @Test
    @DisplayName("장바구니 항목 수정 실패 - 권한 없는 사용자")
    void updateCartItem_fail_unauthorized() {
        UUID userId = UUID.randomUUID();
        UUID cartItemId = UUID.randomUUID();
        CustomUserDetails mockUserDetails = createMockCustomUserDetails(userId);
        CartItem mockCartItem = createMockCartItem(cartItemId, null, null, null, 1, 10000);
        CartItemRequestDTO.CartItemUpdateRequestDTO request = CartItemRequestDTO.CartItemUpdateRequestDTO.builder()
                .quantity(2)
                .menuOptionId(UUID.randomUUID())
                .build();
    
        given(cartItemRepository.findById(cartItemId)).willReturn(Optional.of(mockCartItem));
        given(cartItemRepository.existsByCartItemAndUser(cartItemId, userId)).willReturn(false);
    
        assertThatThrownBy(() -> cartItemCommandService.updateCartItem(request, cartItemId, mockUserDetails))
                .isInstanceOf(CartItemException.class);
    }


    @Test
    @DisplayName("장바구니 항목 삭제 성공")
    void deleteCartItem_success() {
        UUID userId = UUID.randomUUID();
        UUID cartItemId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        CustomUserDetails mockUserDetails = createMockCustomUserDetails(userId);
        User mockUser = createMockUser(userId);
        Cart mockCart = createMockCart(cartId, mockUser);
        CartItem mockCartItem = createMockCartItem(cartItemId, mockCart, null, null, 1, 10000);

        given(cartItemRepository.findById(cartItemId)).willReturn(Optional.of(mockCartItem));
        given(cartRepository.findById(cartId)).willReturn(Optional.of(mockCart));
        given(cartItemRepository.existsByCartItemAndUser(cartItemId, userId)).willReturn(true);

        cartItemCommandService.deleteCartItem(cartItemId, mockUserDetails);

        verify(cartItemRepository, times(1)).delete(mockCartItem);
    }

    @Test
    @DisplayName("장바구니 항목 삭제 실패 - 권한 없는 사용자")
    void deleteCartItem_fail_unauthorized() {
        UUID userId = UUID.randomUUID();
        UUID cartItemId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        CustomUserDetails mockUserDetails = createMockCustomUserDetails(userId);
        User mockUser = createMockUser(userId);
        Cart mockCart = createMockCart(cartId, mockUser);
        CartItem mockCartItem = createMockCartItem(cartItemId, mockCart, null, null, 1, 10000);

        given(cartItemRepository.findById(cartItemId)).willReturn(Optional.of(mockCartItem));
        given(cartRepository.findById(cartId)).willReturn(Optional.of(mockCart));
        given(cartItemRepository.existsByCartItemAndUser(cartItemId, userId)).willReturn(false);

        assertThatThrownBy(() -> cartItemCommandService.deleteCartItem(cartItemId, mockUserDetails))
                .isInstanceOf(CartItemException.class);
    }
}