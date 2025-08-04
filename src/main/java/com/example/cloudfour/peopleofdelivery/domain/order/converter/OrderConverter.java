package com.example.cloudfour.peopleofdelivery.domain.order.converter;

import com.example.cloudfour.peopleofdelivery.domain.order.dto.OrderItemResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.order.dto.OrderRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.order.dto.OrderResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.order.entity.Order;
import com.example.cloudfour.peopleofdelivery.domain.order.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;

public class OrderConverter {
    public static Order toOrder(OrderRequestDTO.OrderCreateRequestDTO orderCreateRequestDTO,Integer totalPrice,String address) {
        return Order.builder()
                .orderType(orderCreateRequestDTO.getOrderType())
                .receiptType(orderCreateRequestDTO.getReceiptType())
                .request(orderCreateRequestDTO.getRequest())
                .address(address)
                .totalPrice(totalPrice)
                .status(orderCreateRequestDTO.getOrderStatus())
                .build();
    }

    public static OrderResponseDTO.OrderCreateResponseDTO toOrderCreateResponseDTO(Order order) {
        return OrderResponseDTO.OrderCreateResponseDTO.builder()
                .orderId(order.getId())
                .orderStatus(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreatedAt())
                .build();
    }

    public static OrderResponseDTO.OrderUpdateResponseDTO toOrderUpdateResponseDTO(Order order, OrderStatus prev_orderStatus) {
        return OrderResponseDTO.OrderUpdateResponseDTO.builder()
                .orderId(order.getId())
                .previousStatus(prev_orderStatus)
                .currentStatus(order.getStatus())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    public static OrderResponseDTO.OrderDetailResponseDTO toOrderDetailResponseDTO(Order order, List<OrderItemResponseDTO.OrderItemListResponseDTO> orderItems) {
        return OrderResponseDTO.OrderDetailResponseDTO.builder()
                .orderId(order.getId())
                .orderType(order.getOrderType())
                .receiptType(order.getReceiptType())
                .orderStatus(order.getStatus())
                .address(order.getAddress())
                .request(order.getRequest())
                .totalPrice(order.getTotalPrice())
                .items(orderItems)
                .createdAt(order.getCreatedAt())
                .build();
    }

    public static OrderResponseDTO.OrderUserResponseDTO toOrderUserResponseDTO(Order order) {
        return OrderResponseDTO.OrderUserResponseDTO.builder()
                .orderId(order.getId())
                .storeName(order.getStore().getName())
                .totalPrice(order.getTotalPrice())
                .orderStatus(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }

    public static OrderResponseDTO.OrderUserListResponseDTO toOrderUserListResponseDTO(List<OrderResponseDTO.OrderUserResponseDTO> orders, Boolean hasNext, LocalDateTime cursor) {
        return OrderResponseDTO.OrderUserListResponseDTO.builder()
                .orderUsers(orders)
                .hasNext(hasNext)
                .cursor(cursor)
                .build();
    }

    public static OrderResponseDTO.OrderStoreResponseDTO toOrderStoreResponseDTO(Order order) {
        return OrderResponseDTO.OrderStoreResponseDTO.builder()
                .orderId(order.getId())
                .userName(order.getUser().getNickname())
                .orderType(order.getOrderType())
                .receiptType(order.getReceiptType())
                .totalPrice(order.getTotalPrice())
                .orderStatus(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }

    public static OrderResponseDTO.OrderStoreListResponseDTO toOrderStoreListResponseDTO(List<OrderResponseDTO.OrderStoreResponseDTO> orders, Boolean hasNext, LocalDateTime cursor) {
        return OrderResponseDTO.OrderStoreListResponseDTO.builder()
                .orderStores(orders)
                .hasNext(hasNext)
                .cursor(cursor)
                .build();
    }
}
