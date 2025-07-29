package com.example.cloudfour.peopleofdelivery.domain.order.service.command;

import com.example.cloudfour.peopleofdelivery.domain.order.dto.OrderRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.order.dto.OrderResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;

import java.util.UUID;

public interface OrderCommandService {
    OrderResponseDTO.OrderCreateResponseDTO createOrder(OrderRequestDTO.OrderCreateRequestDTO orderCreateRequestDTO, User user);

    OrderResponseDTO.OrderUpdateResponseDTO updateOrder(OrderRequestDTO.OrderUpdateRequestDTO orderUpdateRequestDTO, UUID orderId, User user);

    void deleteOrder(UUID orderId, User user);
}
