package com.example.cloudfour.peopleofdelivery.domain.order.dto;

import com.example.cloudfour.peopleofdelivery.domain.order.enums.OrderStatus;
import com.example.cloudfour.peopleofdelivery.domain.order.enums.OrderType;
import com.example.cloudfour.peopleofdelivery.domain.order.enums.ReceiptType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

public class OrderResponseDTO {
    @Getter
    @Builder
    public static class OrderCreateResponseDTO {
        UUID orderId;
        OrderStatus orderStatus;
        Integer totalPrice;
        LocalDateTime createdAt;
    }

    @Getter
    @Builder
    public static class OrderDetailResponseDTO {
        UUID orderId;
        String storeName;
        OrderType orderType;
        ReceiptType receiptType;
        OrderStatus orderStatus;
        String address;
        String request;
        Integer totalPrice;
        LocalDateTime createdAt;
        String items;
    }

    @Getter
    @Builder
    public static class OrderUserListResponseDTO {
        UUID orderId;
        String storeName;
        Integer totalPrice;
        OrderStatus orderStatus;
        LocalDateTime createdAt;
    }

    @Getter
    @Builder
    public static class OrderStoreListResponseDTO {
        UUID orderId;
        String userName;
        OrderType orderType;
        ReceiptType receiptType;
        OrderStatus orderStatus;
        Integer totalPrice;
        LocalDateTime createdAt;
    }

    @Getter
    @Builder
    public static class OrderUpdateResponseDTO {
        UUID orderId;
        String previousStatus;
        String currentStatus;
        LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    public static class OrderStatusResponseDTO {
        OrderStatus orderStatus;
        LocalDateTime updatedAt;
        UUID updatedBy;
    }
}
