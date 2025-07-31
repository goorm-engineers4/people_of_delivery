package com.example.cloudfour.peopleofdelivery.domain.order.service.command;

import com.example.cloudfour.peopleofdelivery.domain.order.converter.OrderConverter;
import com.example.cloudfour.peopleofdelivery.domain.order.dto.OrderRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.order.dto.OrderResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.order.entity.Order;
import com.example.cloudfour.peopleofdelivery.domain.order.enums.OrderStatus;
import com.example.cloudfour.peopleofdelivery.domain.order.exception.OrderErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.order.exception.OrderException;
import com.example.cloudfour.peopleofdelivery.domain.order.repository.OrderRepository;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.store.exception.StoreErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.store.exception.StoreException;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.domain.user.exception.UserErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.user.exception.UserException;
import com.example.cloudfour.peopleofdelivery.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderCommandServiceImpl {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    public OrderResponseDTO.OrderCreateResponseDTO createOrder(OrderRequestDTO.OrderCreateRequestDTO orderCreateRequestDTO, User user) {
        userRepository.findById(user.getId()).orElseThrow(()->new UserException(UserErrorCode.NOT_FOUND));
        Store findStore = storeRepository.findById(orderCreateRequestDTO.getStoreId()).orElseThrow(()->new StoreException(StoreErrorCode.NOT_FOUND));
        Order order = OrderConverter.toOrder(orderCreateRequestDTO);
        order.setStore(findStore);
        order.setUser(user);
        orderRepository.save(order);
        return OrderConverter.toOrderCreateResponseDTO(order);
    }

    public OrderResponseDTO.OrderUpdateResponseDTO updateOrder(OrderRequestDTO.OrderUpdateRequestDTO orderUpdateRequestDTO, UUID orderId, User user) {
        userRepository.findById(user.getId()).orElseThrow(()->new UserException(UserErrorCode.NOT_FOUND));
        if(!orderRepository.existsByOrderIdAndUserId(orderId, user.getId())) {
            throw new OrderException(OrderErrorCode.UNAUTHORIZED_ACCESS);
        }
        Order order = orderRepository.findById(orderId).orElseThrow(()->new OrderException(OrderErrorCode.NOT_FOUND));
        OrderStatus prev_orderStatus = order.getStatus();
        order.updateOrderStatus(orderUpdateRequestDTO.getNewStatus());
        // 주문 상태 변경하기
        orderRepository.save(order);
        return OrderConverter.toOrderUpdateResponseDTO(order,prev_orderStatus);
    }

    public void deleteOrder(UUID orderId, User user) {
        userRepository.findById(user.getId()).orElseThrow(()->new UserException(UserErrorCode.NOT_FOUND));
        if(!orderRepository.existsByOrderIdAndUserId(orderId, user.getId())) {
            throw new OrderException(OrderErrorCode.UNAUTHORIZED_ACCESS);
        }
        Order order = orderRepository.findById(orderId).orElseThrow(()->new OrderException(OrderErrorCode.NOT_FOUND));
        order.softDelete();
    }
}
