package com.example.cloudfour.peopleofdelivery.domain.order.dto;

import com.example.cloudfour.peopleofdelivery.domain.order.enums.OrderStatus;
import com.example.cloudfour.peopleofdelivery.domain.order.enums.OrderType;
import com.example.cloudfour.peopleofdelivery.domain.order.enums.ReceiptType;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

public class OrderRequestDTO {
    @Getter
    @Builder
    public static class OrderCreateRequestDTO {
        OrderType orderType;
        OrderStatus orderStatus;
        ReceiptType receiptType;
        String request;
        UUID address;
    }

    @Getter
    @Builder
    public static class OrderUpdateRequestDTO {
        OrderStatus newStatus;
    }
}
