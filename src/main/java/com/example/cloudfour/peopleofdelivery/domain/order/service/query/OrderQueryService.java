package com.example.cloudfour.peopleofdelivery.domain.order.service.query;

import com.example.cloudfour.peopleofdelivery.domain.order.dto.OrderResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;

import java.util.List;
import java.util.UUID;

public interface OrderQueryService {
    OrderResponseDTO.OrderDetailResponseDTO getOrderById(UUID reviewId, User user);

    List<OrderResponseDTO.OrderUserListResponseDTO> getOrderListByUser(User user);

    List<OrderResponseDTO.OrderStoreListResponseDTO> getOrderListByStore(UUID storeId,User user);

    OrderResponseDTO.OrderStatusResponseDTO getOrderStatusById(UUID reviewId, User user);
}