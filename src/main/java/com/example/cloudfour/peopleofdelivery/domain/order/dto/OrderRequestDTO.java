package com.example.cloudfour.peopleofdelivery.domain.order.dto;

import com.example.cloudfour.peopleofdelivery.domain.order.enums.OrderStatus;
import com.example.cloudfour.peopleofdelivery.domain.order.enums.OrderType;
import com.example.cloudfour.peopleofdelivery.domain.order.enums.ReceiptType;
import com.example.cloudfour.peopleofdelivery.domain.order.entity.OrderItem;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

public class OrderRequestDTO {
    @Getter
    @Builder
    public static class OrderCreateRequestDTO {
        UUID storeId;
        OrderType orderType;
        OrderStatus orderStatus;
        ReceiptType receiptType;
        String address;
        String request;
        Integer totalPrice;
        List<OrderItem> items;
        UUID menuId;
        List<UUID> menuOptionIds;
        Integer quantity;
    }

    @Getter
    @Builder
    public static class OrderUpdateRequestDTO {
        OrderStatus newStatus;
    }
}
