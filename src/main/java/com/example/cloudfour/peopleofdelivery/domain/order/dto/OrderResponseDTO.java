package com.example.cloudfour.peopleofdelivery.domain.order.dto;

import com.example.cloudfour.peopleofdelivery.domain.order.entity.Order;
import com.example.cloudfour.peopleofdelivery.domain.order.enums.OrderStatus;
import com.example.cloudfour.peopleofdelivery.domain.order.enums.OrderType;
import com.example.cloudfour.peopleofdelivery.domain.order.enums.ReceiptType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
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
        List<OrderItemResponseDTO.OrderItemListResponseDTO> items;
    }

    @Getter
    @Builder
    public static class OrderUserResponseDTO {
        UUID orderId;
        String storeName;
        Integer totalPrice;
        OrderStatus orderStatus;
        LocalDateTime createdAt;
    }

    @Getter
    @Builder
    public static class OrderUserListResponseDTO {
        List<OrderUserResponseDTO>  orderUsers;
        private boolean hasNext;
        private LocalDateTime cursor;
    }

    @Getter
    @Builder
    public static class OrderStoreResponseDTO {
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
    public static class OrderStoreListResponseDTO {
        List<OrderStoreResponseDTO> orderStores;
        private boolean hasNext;
        private LocalDateTime cursor;
    }

    @Getter
    @Builder
    public static class OrderUpdateResponseDTO {
        UUID orderId;
        OrderStatus previousStatus;
        OrderStatus currentStatus;
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
