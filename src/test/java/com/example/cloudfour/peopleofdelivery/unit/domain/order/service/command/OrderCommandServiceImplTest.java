package com.example.cloudfour.peopleofdelivery.unit.domain.order.service.command;

import com.example.cloudfour.peopleofdelivery.domain.cart.entity.Cart;
import com.example.cloudfour.peopleofdelivery.domain.cart.exception.CartErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.cart.exception.CartException;
import com.example.cloudfour.peopleofdelivery.domain.cart.repository.CartRepository;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.entity.CartItem;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.exception.CartItemException;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.repository.CartItemRepository;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.Menu;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuOption;
import com.example.cloudfour.peopleofdelivery.domain.order.dto.OrderRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.order.dto.OrderResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.order.entity.Order;
import com.example.cloudfour.peopleofdelivery.domain.order.enums.OrderStatus;
import com.example.cloudfour.peopleofdelivery.domain.order.enums.OrderType;
import com.example.cloudfour.peopleofdelivery.domain.order.enums.ReceiptType;
import com.example.cloudfour.peopleofdelivery.domain.order.exception.OrderErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.order.exception.OrderException;
import com.example.cloudfour.peopleofdelivery.domain.order.repository.OrderItemRepository;
import com.example.cloudfour.peopleofdelivery.domain.order.repository.OrderRepository;
import com.example.cloudfour.peopleofdelivery.domain.order.service.command.OrderCommandServiceImpl;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.UserAddress;
import com.example.cloudfour.peopleofdelivery.domain.user.exception.UserAddressErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.user.exception.UserAddressException;
import com.example.cloudfour.peopleofdelivery.domain.user.exception.UserErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.user.exception.UserException;
import com.example.cloudfour.peopleofdelivery.domain.user.repository.UserAddressRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.repository.UserRepository;
import com.example.cloudfour.peopleofdelivery.global.auth.userdetails.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("주문 커맨드 서비스 테스트")
class OrderCommandServiceImplTest {

    @InjectMocks
    private OrderCommandServiceImpl orderCommandService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserAddressRepository userAddressRepository;

    @Mock
    private CustomUserDetails userDetails;

    private User user;
    private Store store;
    private Cart cart;
    private CartItem cartItem;
    private Menu menu;
    private MenuOption menuOption;
    private UserAddress userAddress;
    private Order order;
    private UUID userId;
    private UUID cartId;
    private UUID storeId;
    private UUID orderId;
    private UUID addressId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        cartId = UUID.randomUUID();
        storeId = UUID.randomUUID();
        orderId = UUID.randomUUID();
        addressId = UUID.randomUUID();

        user = mock(User.class);
        store = mock(Store.class);
        menu = mock(Menu.class);
        menuOption = mock(MenuOption.class);
        userAddress = mock(UserAddress.class);
        order = mock(Order.class);
        cart = mock(Cart.class);
        cartItem = mock(CartItem.class);

        when(userDetails.getId()).thenReturn(userId);
    }

    @Test
    @DisplayName("주문 생성 - 성공")
    void createOrder_Success() {
        OrderRequestDTO.OrderCreateRequestDTO request = OrderRequestDTO.OrderCreateRequestDTO.builder()
                .orderType(OrderType.온라인)
                .receiptType(ReceiptType.배달)
                .address(addressId)
                .request("빠른 배송 부탁드립니다")
                .build();

        when(userAddress.getId()).thenReturn(addressId);
        when(cart.getStore()).thenReturn(store);
        when(store.getId()).thenReturn(storeId);
        when(userAddress.getAddress()).thenReturn("서울 종로구 청운동 100-1");
        when(cartItem.getPrice()).thenReturn(19000);
        when(cartItem.getMenu()).thenReturn(menu);
        when(cartItem.getMenuOption()).thenReturn(menuOption);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartRepository.existsByUserAndCart(userId, cartId)).thenReturn(true);
        when(userAddressRepository.findById(addressId)).thenReturn(Optional.of(userAddress));
        when(userAddressRepository.existsByUserIdAndAddressId(userId, addressId)).thenReturn(true);
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(cartItemRepository.findAllByCartId(cartId, userId)).thenReturn(List.of(cartItem));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderItemRepository.saveAll(anyList())).thenReturn(List.of());
        doNothing().when(cartRepository).delete(cart);

        OrderResponseDTO.OrderCreateResponseDTO result = orderCommandService.createOrder(request, cartId, userDetails);

        assertThat(result).isNotNull();
        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository).saveAll(anyList());
        verify(cartRepository).delete(cart);
    }

    @Test
    @DisplayName("주문 생성 - 사용자를 찾을 수 없음")
    void createOrder_UserNotFound() {
        OrderRequestDTO.OrderCreateRequestDTO request = OrderRequestDTO.OrderCreateRequestDTO.builder()
                .orderType(OrderType.온라인)
                .receiptType(ReceiptType.배달)
                .address(addressId)
                .request("빠른 배송 부탁드립니다")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderCommandService.createOrder(request, cartId, userDetails))
                .isInstanceOf(UserException.class)
                .hasMessage(UserErrorCode.NOT_FOUND.getMessage());

        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("주문 생성 - 장바구니를 찾을 수 없음")
    void createOrder_CartNotFound() {
        OrderRequestDTO.OrderCreateRequestDTO request = OrderRequestDTO.OrderCreateRequestDTO.builder()
                .orderType(OrderType.온라인)
                .receiptType(ReceiptType.배달)
                .address(addressId)
                .request("빠른 배송 부탁드립니다")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderCommandService.createOrder(request, cartId, userDetails))
                .isInstanceOf(CartException.class)
                .hasMessage(CartErrorCode.NOT_FOUND.getMessage());

        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("주문 생성 - 장바구니 접근 권한 없음")
    void createOrder_UnauthorizedCartAccess() {
        OrderRequestDTO.OrderCreateRequestDTO request = OrderRequestDTO.OrderCreateRequestDTO.builder()
                .orderType(OrderType.온라인)
                .receiptType(ReceiptType.배달)
                .address(addressId)
                .request("빠른 배송 부탁드립니다")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartRepository.existsByUserAndCart(userId, cartId)).thenReturn(false);

        assertThatThrownBy(() -> orderCommandService.createOrder(request, cartId, userDetails))
                .isInstanceOf(OrderException.class)
                .hasMessage(OrderErrorCode.UNAUTHORIZED_ACCESS.getMessage());

        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("주문 생성 - 주소를 찾을 수 없음")
    void createOrder_AddressNotFound() {
        OrderRequestDTO.OrderCreateRequestDTO request = OrderRequestDTO.OrderCreateRequestDTO.builder()
                .orderType(OrderType.온라인)
                .receiptType(ReceiptType.배달)
                .address(addressId)
                .request("빠른 배송 부탁드립니다")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartRepository.existsByUserAndCart(userId, cartId)).thenReturn(true);
        when(userAddressRepository.findById(addressId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderCommandService.createOrder(request, cartId, userDetails))
                .isInstanceOf(UserAddressException.class)
                .hasMessage(UserAddressErrorCode.NOT_FOUND.getMessage());

        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("주문 생성 - 주소 접근 권한 없음")
    void createOrder_UnauthorizedAddressAccess() {
        OrderRequestDTO.OrderCreateRequestDTO request = OrderRequestDTO.OrderCreateRequestDTO.builder()
                .orderType(OrderType.온라인)
                .receiptType(ReceiptType.배달)
                .address(addressId)
                .request("빠른 배송 부탁드립니다")
                .build();

        when(userAddress.getId()).thenReturn(addressId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartRepository.existsByUserAndCart(userId, cartId)).thenReturn(true);
        when(userAddressRepository.findById(addressId)).thenReturn(Optional.of(userAddress));
        when(userAddressRepository.existsByUserIdAndAddressId(userId, addressId)).thenReturn(false);

        assertThatThrownBy(() -> orderCommandService.createOrder(request, cartId, userDetails))
                .isInstanceOf(OrderException.class)
                .hasMessage(OrderErrorCode.UNAUTHORIZED_ACCESS.getMessage());

        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("주문 생성 - 장바구니가 비어있음")
    void createOrder_EmptyCart() {
        OrderRequestDTO.OrderCreateRequestDTO request = OrderRequestDTO.OrderCreateRequestDTO.builder()
                .orderType(OrderType.온라인)
                .receiptType(ReceiptType.배달)
                .address(addressId)
                .request("빠른 배송 부탁드립니다")
                .build();

        when(userAddress.getId()).thenReturn(addressId);
        when(cart.getStore()).thenReturn(store);
        when(store.getId()).thenReturn(storeId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartRepository.existsByUserAndCart(userId, cartId)).thenReturn(true);
        when(userAddressRepository.findById(addressId)).thenReturn(Optional.of(userAddress));
        when(userAddressRepository.existsByUserIdAndAddressId(userId, addressId)).thenReturn(true);
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(cartItemRepository.findAllByCartId(cartId, userId)).thenReturn(List.of());

        assertThatThrownBy(() -> orderCommandService.createOrder(request, cartId, userDetails))
                .isInstanceOf(CartItemException.class)
                .hasMessage(CartErrorCode.NOT_FOUND.getMessage());

        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("주문 상태 업데이트 - 성공")
    void updateOrder_Success() {
        OrderRequestDTO.OrderUpdateRequestDTO request = OrderRequestDTO.OrderUpdateRequestDTO.builder()
                .newStatus(OrderStatus.배달중)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderRepository.existsByOrderIdAndUserId(orderId, userId)).thenReturn(true);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponseDTO.OrderUpdateResponseDTO result = orderCommandService.updateOrder(request, orderId, userDetails);

        assertThat(result).isNotNull();
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 상태 업데이트 - 주문 접근 권한 없음")
    void updateOrder_UnauthorizedAccess() {
        OrderRequestDTO.OrderUpdateRequestDTO request = OrderRequestDTO.OrderUpdateRequestDTO.builder()
                .newStatus(OrderStatus.배달중)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderRepository.existsByOrderIdAndUserId(orderId, userId)).thenReturn(false);

        assertThatThrownBy(() -> orderCommandService.updateOrder(request, orderId, userDetails))
                .isInstanceOf(OrderException.class)
                .hasMessage(OrderErrorCode.UNAUTHORIZED_ACCESS.getMessage());

        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("주문 상태 업데이트 - 주문을 찾을 수 없음")
    void updateOrder_OrderNotFound() {
        OrderRequestDTO.OrderUpdateRequestDTO request = OrderRequestDTO.OrderUpdateRequestDTO.builder()
                .newStatus(OrderStatus.배달중)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderRepository.existsByOrderIdAndUserId(orderId, userId)).thenReturn(true);
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderCommandService.updateOrder(request, orderId, userDetails))
                .isInstanceOf(OrderException.class)
                .hasMessage(OrderErrorCode.NOT_FOUND.getMessage());

        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("주문 삭제 - 성공")
    void deleteOrder_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderRepository.existsByOrderIdAndUserId(orderId, userId)).thenReturn(true);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderCommandService.deleteOrder(orderId, userDetails);

        verify(userRepository).findById(userId);
        verify(orderRepository).existsByOrderIdAndUserId(orderId, userId);
        verify(orderRepository).findById(orderId);
    }

    @Test
    @DisplayName("주문 삭제 - 사용자를 찾을 수 없음")
    void deleteOrder_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderCommandService.deleteOrder(orderId, userDetails))
                .isInstanceOf(UserException.class)
                .hasMessage(UserErrorCode.NOT_FOUND.getMessage());

        verify(orderRepository, never()).findById(any());
    }

    @Test
    @DisplayName("주문 삭제 - 주문 접근 권한 없음")
    void deleteOrder_UnauthorizedAccess() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderRepository.existsByOrderIdAndUserId(orderId, userId)).thenReturn(false);

        assertThatThrownBy(() -> orderCommandService.deleteOrder(orderId, userDetails))
                .isInstanceOf(OrderException.class)
                .hasMessage(OrderErrorCode.UNAUTHORIZED_ACCESS.getMessage());

        verify(orderRepository, never()).findById(any());
    }

    @Test
    @DisplayName("주문 삭제 - 주문을 찾을 수 없음")
    void deleteOrder_OrderNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderRepository.existsByOrderIdAndUserId(orderId, userId)).thenReturn(true);
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderCommandService.deleteOrder(orderId, userDetails))
                .isInstanceOf(OrderException.class)
                .hasMessage(OrderErrorCode.NOT_FOUND.getMessage());
    }
}
