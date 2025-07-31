package com.example.cloudfour.peopleofdelivery.domain.order.dto;

import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuOption;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class OrderItemResponseDTO {
    @Getter
    @Builder
    public static class OrderItemListResponseDTO{
        String menuName;
        Integer quantity;
        Integer price;

        //TODO MenuOptionDTO로 변경하기
        List<MenuOption> optionList;
    }
}
