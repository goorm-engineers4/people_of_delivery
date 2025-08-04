package com.example.cloudfour.peopleofdelivery.domain.order.service.query;

import com.example.cloudfour.peopleofdelivery.domain.menu.converter.MenuOptionConverter;
import com.example.cloudfour.peopleofdelivery.domain.order.converter.OrderConverter;
import com.example.cloudfour.peopleofdelivery.domain.order.converter.OrderItemConverter;
import com.example.cloudfour.peopleofdelivery.domain.order.dto.OrderItemResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.order.dto.OrderResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.order.entity.Order;
import com.example.cloudfour.peopleofdelivery.domain.order.entity.OrderItem;
import com.example.cloudfour.peopleofdelivery.domain.order.exception.OrderErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.order.exception.OrderException;
import com.example.cloudfour.peopleofdelivery.domain.order.repository.OrderItemRepository;
import com.example.cloudfour.peopleofdelivery.domain.order.repository.OrderRepository;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.exception.UserErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.user.exception.UserException;
import com.example.cloudfour.peopleofdelivery.domain.user.repository.UserRepository;
import com.example.cloudfour.peopleofdelivery.global.auth.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderQueryServiceImpl {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private static final LocalDateTime first_cursor = LocalDateTime.now().plusDays(1);
    private final StoreRepository storeRepository;

    public OrderResponseDTO.OrderDetailResponseDTO getOrderById(UUID orderId , CustomUserDetails user) {
        userRepository.findById(user.getId()).orElseThrow(()->new UserException(UserErrorCode.NOT_FOUND));
        Order order = orderRepository.findById(orderId).orElseThrow(()->new OrderException(OrderErrorCode.NOT_FOUND));
        if(!orderRepository.existsByOrderIdAndUserId(orderId, user.getId())) {
            throw new OrderException(OrderErrorCode.UNAUTHORIZED_ACCESS);
        }
        List<OrderItem> orderItems =  orderItemRepository.findByOrderId(orderId);
        List<OrderItemResponseDTO.OrderItemListResponseDTO> orderItemDTOS = orderItems.stream().map(orderItem ->
                OrderItemConverter.toOrderItemClassListDTO(orderItem, MenuOptionConverter.toMenuOptionSimpleResponseDTO(orderItem.getMenuOption()))).toList();
        return OrderConverter.toOrderDetailResponseDTO(order,orderItemDTOS);
    }

    public OrderResponseDTO.OrderUserListResponseDTO getOrderListByUser(CustomUserDetails user, LocalDateTime cursor, Integer size) {
        userRepository.findById(user.getId()).orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));
        if(cursor == null) {
            cursor = first_cursor;
        }
        Pageable pageable = PageRequest.of(0, size);
        Slice<Order> orders = orderRepository.findAllByUserId(user.getId(),cursor,pageable);
        if(orders.isEmpty()) {
            throw new OrderException(OrderErrorCode.NOT_FOUND);
        }
        List<Order> orderList = orders.toList();
        List<OrderResponseDTO.OrderUserResponseDTO>  orderUserResponseDTOS = orderList.stream().map(OrderConverter::toOrderUserResponseDTO).toList();
        LocalDateTime next_cursor = null;
        if(!orderList.isEmpty() && orders.hasNext()) {
            next_cursor = orderList.getLast().getCreatedAt();
        }
        return OrderConverter.toOrderUserListResponseDTO(orderUserResponseDTOS,orders.hasNext(),next_cursor);
    }

    public OrderResponseDTO.OrderStoreListResponseDTO getOrderListByStore(UUID storeId, LocalDateTime cursor, Integer size, CustomUserDetails user) {
        userRepository.findById(user.getId()).orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));
        if(!storeRepository.existsByStoreAndUser(storeId, user.getId())) {
            throw new OrderException(OrderErrorCode.UNAUTHORIZED_ACCESS);
        }
        if(cursor == null) {
            cursor = first_cursor;
        }
        Pageable pageable = PageRequest.of(0, size);
        Slice<Order> orders = orderRepository.findAllByStoreId(storeId,cursor,pageable);
        if(orders.isEmpty()) {
            throw new OrderException(OrderErrorCode.NOT_FOUND);
        }
        List<Order> orderList = orders.toList();
        List<OrderResponseDTO.OrderStoreResponseDTO> orderStoreResponseDTOS = orderList.stream().map(OrderConverter::toOrderStoreResponseDTO).toList();
        LocalDateTime next_cursor = null;
        if(!orderList.isEmpty() && orders.hasNext()) {
            next_cursor = orderList.getLast().getCreatedAt();
        }
        return OrderConverter.toOrderStoreListResponseDTO(orderStoreResponseDTOS,orders.hasNext(),next_cursor);
    }
}
