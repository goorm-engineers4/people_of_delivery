package com.example.cloudfour.peopleofdelivery.domain.order.converter;

import com.example.cloudfour.peopleofdelivery.domain.cartitem.entity.CartItem;
import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuOptionResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.order.dto.OrderItemResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.order.entity.Order;
import com.example.cloudfour.peopleofdelivery.domain.order.entity.OrderItem;

public class OrderItemConverter {
    public static OrderItemResponseDTO.OrderItemListResponseDTO toOrderItemClassListDTO(OrderItem orderItem, MenuOptionResponseDTO.MenuOptionSimpleResponseDTO option) {
        return OrderItemResponseDTO.OrderItemListResponseDTO.builder()
                .option(option)
                .price(orderItem.getPrice())
                .quantity(orderItem.getQuantity())
                .menuName(orderItem.getMenu().getName())
                .build();
    }

    public static OrderItem CartItemtoOrderItem(CartItem cartItem, Order order) {
        OrderItem orderItem = OrderItem.builder()
                .quantity(cartItem.getQuantity())
                .price(cartItem.getPrice())
                .build();
        orderItem.setMenu(cartItem.getMenu());
        orderItem.setOrder(order);
        orderItem.setMenuOption(cartItem.getMenuOption());
        return orderItem;

    }
}