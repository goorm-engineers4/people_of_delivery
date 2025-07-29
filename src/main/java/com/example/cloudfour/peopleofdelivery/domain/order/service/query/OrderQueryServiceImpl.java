package com.example.cloudfour.peopleofdelivery.domain.order.service.query;

import com.example.cloudfour.peopleofdelivery.domain.order.dto.OrderResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderQueryServiceImpl {
    public OrderResponseDTO.OrderDetailResponseDTO getOrderById(UUID reviewId, User user) {
        return null;
    }

    public List<OrderResponseDTO.OrderUserListResponseDTO> getOrderListByUser(User user) {
        return List.of();
    }

    public List<OrderResponseDTO.OrderStoreListResponseDTO> getOrderListByStore(UUID storeId, User user) {
        return List.of();
    }

    public OrderResponseDTO.OrderStatusResponseDTO getOrderStatusById(UUID reviewId, User user) {
        return null;
    }
}
