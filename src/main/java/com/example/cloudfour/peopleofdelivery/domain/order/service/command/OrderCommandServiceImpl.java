package com.example.cloudfour.peopleofdelivery.domain.order.service.command;

import com.example.cloudfour.peopleofdelivery.domain.order.dto.OrderRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.order.dto.OrderResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
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
    public OrderResponseDTO.OrderCreateResponseDTO createOrder(OrderRequestDTO.OrderCreateRequestDTO orderCreateRequestDTO, User user) {
        return null;
    }

    public OrderResponseDTO.OrderUpdateResponseDTO updateOrder(OrderRequestDTO.OrderUpdateRequestDTO orderUpdateRequestDTO, UUID orderId, User user) {
        return null;
    }

    public void deleteOrder(UUID orderId, User user) {

    }
}
