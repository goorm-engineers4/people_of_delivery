package com.example.cloudfour.peopleofdelivery.unit.domain.order.service.query;

import com.example.cloudfour.peopleofdelivery.domain.order.dto.OrderResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.order.entity.Order;
import com.example.cloudfour.peopleofdelivery.domain.order.entity.OrderItem;
import com.example.cloudfour.peopleofdelivery.domain.order.exception.OrderErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.order.exception.OrderException;
import com.example.cloudfour.peopleofdelivery.domain.order.repository.OrderItemRepository;
import com.example.cloudfour.peopleofdelivery.domain.order.repository.OrderRepository;
import com.example.cloudfour.peopleofdelivery.domain.order.service.query.OrderQueryServiceImpl;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.domain.user.exception.UserErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.user.exception.UserException;
import com.example.cloudfour.peopleofdelivery.domain.user.repository.UserRepository;
import com.example.cloudfour.peopleofdelivery.fixtures.Factory;
import com.example.cloudfour.peopleofdelivery.global.auth.userdetails.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("주문 쿼리 서비스 테스트")
class OrderQueryServiceImplTest {

    @InjectMocks
    private OrderQueryServiceImpl orderQueryService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private CustomUserDetails userDetails;

    private User user;
    private Order order;
    private OrderItem orderItem;
    private UUID userId;
    private UUID orderId;
    private UUID storeId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        orderId = UUID.randomUUID();
        storeId = UUID.randomUUID();

        user = Factory.createMockUserWithAll();
        order = user.getOrders().get(0);
        orderItem = order.getOrderItems().get(0);

        try {
            Field createdAtField = order.getClass().getSuperclass().getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(order, LocalDateTime.now());
        } catch (Exception e) {

        }

        when(userDetails.getId()).thenReturn(userId);
    }

    @Test
    @DisplayName("주문 상세 조회 - 성공")
    void getOrderById_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.existsByOrderIdAndUserId(orderId, userId)).thenReturn(true);
        when(orderItemRepository.findByOrderId(orderId)).thenReturn(List.of(orderItem));

        OrderResponseDTO.OrderDetailResponseDTO result = orderQueryService.getOrderById(orderId, userDetails);

        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(1);
    }

    @Test
    @DisplayName("주문 상세 조회 - 사용자를 찾을 수 없음")
    void getOrderById_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderQueryService.getOrderById(orderId, userDetails))
                .isInstanceOf(UserException.class)
                .hasMessage(UserErrorCode.NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("주문 상세 조회 - 주문을 찾을 수 없음")
    void getOrderById_OrderNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderQueryService.getOrderById(orderId, userDetails))
                .isInstanceOf(OrderException.class)
                .hasMessage(OrderErrorCode.NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("주문 상세 조회 - 접근 권한 없음")
    void getOrderById_UnauthorizedAccess() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.existsByOrderIdAndUserId(orderId, userId)).thenReturn(false);

        assertThatThrownBy(() -> orderQueryService.getOrderById(orderId, userDetails))
                .isInstanceOf(OrderException.class)
                .hasMessage(OrderErrorCode.UNAUTHORIZED_ACCESS.getMessage());
    }

    @Test
    @DisplayName("사용자별 주문 목록 조회 - 성공")
    void getOrderListByUser_Success() {
        LocalDateTime cursor = LocalDateTime.now();
        Integer size = 10;

        Slice<Order> orderSlice = new SliceImpl<>(List.of(order), Pageable.ofSize(size), false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderRepository.findAllByUserId(eq(userId), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(orderSlice);

        OrderResponseDTO.OrderUserListResponseDTO result = orderQueryService.getOrderListByUser(userDetails, cursor, size);

        assertThat(result).isNotNull();
        assertThat(result.getOrderUsers()).hasSize(1);
        assertThat(result.isHasNext()).isFalse();
    }

    @Test
    @DisplayName("사용자별 주문 목록 조회 - 커서가 null인 경우")
    void getOrderListByUser_NullCursor() {
        Integer size = 10;

        Slice<Order> orderSlice = new SliceImpl<>(List.of(order), Pageable.ofSize(size), false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderRepository.findAllByUserId(eq(userId), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(orderSlice);

        OrderResponseDTO.OrderUserListResponseDTO result = orderQueryService.getOrderListByUser(userDetails, null, size);

        assertThat(result).isNotNull();
        assertThat(result.getOrderUsers()).hasSize(1);
    }

    @Test
    @DisplayName("사용자별 주문 목록 조회 - 사용자를 찾을 수 없음")
    void getOrderListByUser_UserNotFound() {
        LocalDateTime cursor = LocalDateTime.now();
        Integer size = 10;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderQueryService.getOrderListByUser(userDetails, cursor, size))
                .isInstanceOf(UserException.class)
                .hasMessage(UserErrorCode.NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("사용자별 주문 목록 조회 - 주문이 없음")
    void getOrderListByUser_NoOrders() {
        LocalDateTime cursor = LocalDateTime.now();
        Integer size = 10;

        Slice<Order> emptySlice = new SliceImpl<>(List.of(), Pageable.ofSize(size), false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderRepository.findAllByUserId(eq(userId), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(emptySlice);

        assertThatThrownBy(() -> orderQueryService.getOrderListByUser(userDetails, cursor, size))
                .isInstanceOf(OrderException.class)
                .hasMessage(OrderErrorCode.NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("사용자별 주문 목록 조회 - 다음 페이지 있음")
    void getOrderListByUser_HasNext() {
        LocalDateTime cursor = LocalDateTime.now();
        Integer size = 10;

        Slice<Order> orderSlice = new SliceImpl<>(List.of(order), Pageable.ofSize(size), true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderRepository.findAllByUserId(eq(userId), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(orderSlice);

        OrderResponseDTO.OrderUserListResponseDTO result = orderQueryService.getOrderListByUser(userDetails, cursor, size);

        assertThat(result).isNotNull();
        assertThat(result.isHasNext()).isTrue();
        assertThat(result.getCursor()).isNotNull();
    }

    @Test
    @DisplayName("매장별 주문 목록 조회 - 성공")
    void getOrderListByStore_Success() {
        LocalDateTime cursor = LocalDateTime.now();
        Integer size = 10;

        Slice<Order> orderSlice = new SliceImpl<>(List.of(order), Pageable.ofSize(size), false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(storeRepository.existsByStoreAndUser(storeId, userId)).thenReturn(true);
        when(orderRepository.findAllByStoreId(eq(storeId), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(orderSlice);

        OrderResponseDTO.OrderStoreListResponseDTO result = orderQueryService.getOrderListByStore(storeId, cursor, size, userDetails);

        assertThat(result).isNotNull();
        assertThat(result.getOrderStores()).hasSize(1);
        assertThat(result.isHasNext()).isFalse();
    }

    @Test
    @DisplayName("매장별 주문 목록 조회 - 매장 접근 권한 없음")
    void getOrderListByStore_UnauthorizedStoreAccess() {
        LocalDateTime cursor = LocalDateTime.now();
        Integer size = 10;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(storeRepository.existsByStoreAndUser(storeId, userId)).thenReturn(false);

        assertThatThrownBy(() -> orderQueryService.getOrderListByStore(storeId, cursor, size, userDetails))
                .isInstanceOf(OrderException.class)
                .hasMessage(OrderErrorCode.UNAUTHORIZED_ACCESS.getMessage());
    }

    @Test
    @DisplayName("매장별 주문 목록 조회 - 주문이 없음")
    void getOrderListByStore_NoOrders() {
        LocalDateTime cursor = LocalDateTime.now();
        Integer size = 10;

        Slice<Order> emptySlice = new SliceImpl<>(List.of(), Pageable.ofSize(size), false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(storeRepository.existsByStoreAndUser(storeId, userId)).thenReturn(true);
        when(orderRepository.findAllByStoreId(eq(storeId), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(emptySlice);

        assertThatThrownBy(() -> orderQueryService.getOrderListByStore(storeId, cursor, size, userDetails))
                .isInstanceOf(OrderException.class)
                .hasMessage(OrderErrorCode.NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("매장별 주문 목록 조회 - 커서가 null인 경우")
    void getOrderListByStore_NullCursor() {
        Integer size = 10;

        Slice<Order> orderSlice = new SliceImpl<>(List.of(order), Pageable.ofSize(size), false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(storeRepository.existsByStoreAndUser(storeId, userId)).thenReturn(true);
        when(orderRepository.findAllByStoreId(eq(storeId), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(orderSlice);

        OrderResponseDTO.OrderStoreListResponseDTO result = orderQueryService.getOrderListByStore(storeId, null, size, userDetails);

        assertThat(result).isNotNull();
        assertThat(result.getOrderStores()).hasSize(1);
    }

    @Test
    @DisplayName("매장별 주문 목록 조회 - 다음 페이지 있음")
    void getOrderListByStore_HasNext() {
        LocalDateTime cursor = LocalDateTime.now();
        Integer size = 10;

        Slice<Order> orderSlice = new SliceImpl<>(List.of(order), Pageable.ofSize(size), true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(storeRepository.existsByStoreAndUser(storeId, userId)).thenReturn(true);
        when(orderRepository.findAllByStoreId(eq(storeId), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(orderSlice);

        OrderResponseDTO.OrderStoreListResponseDTO result = orderQueryService.getOrderListByStore(storeId, cursor, size, userDetails);

        assertThat(result).isNotNull();
        assertThat(result.isHasNext()).isTrue();
        assertThat(result.getCursor()).isNotNull();
    }
}
