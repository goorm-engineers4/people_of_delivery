package com.example.cloudfour.peopleofdelivery.domain.order.converter;

import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuOption;
import com.example.cloudfour.peopleofdelivery.domain.order.dto.OrderItemResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.order.entity.OrderItem;

import java.util.List;

public class OrderItemConverter {
    public static OrderItemResponseDTO.OrderItemListResponseDTO toOrderItemClassListDTO(OrderItem orderItem, List<MenuOption> menuOption) {
        return OrderItemResponseDTO.OrderItemListResponseDTO.builder()
                .optionList(menuOption)
                .price(orderItem.getPrice())
                .quantity(orderItem.getQuantity())
                .menuName(orderItem.getMenu().getName())
                .build();
    }
}