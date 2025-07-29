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
public class OrderQueryServiceImpl implements OrderQueryService {
    @Override
    public OrderResponseDTO.OrderDetailResponseDTO getOrderById(UUID reviewId, User user) {
        return null;
    }

    @Override
    public List<OrderResponseDTO.OrderUserListResponseDTO> getOrderListByUser(User user) {
        return List.of();
    }

    @Override
    public List<OrderResponseDTO.OrderStoreListResponseDTO> getOrderListByStore(UUID storeId, User user) {
        return List.of();
    }

    @Override
    public OrderResponseDTO.OrderStatusResponseDTO getOrderStatusById(UUID reviewId, User user) {
        return null;
    }
}
