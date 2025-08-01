package com.example.cloudfour.peopleofdelivery.domain.menu.converter;

import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.Menu;

public class MenuConverter {

    public static Menu toMenu(MenuRequestDTO.MenuCreateRequestDTO requestDTO) {
        return Menu.builder()
                .name(requestDTO.getName())
                .content(requestDTO.getContent())
                .price(requestDTO.getPrice())
                .menuPicture(requestDTO.getMenuPicture())
                .status(requestDTO.getStatus())
                .build();
    }

    public static MenuResponseDTO.MenuDetailResponseDTO toMenuDetailResponseDTO(Menu menu) {
        return MenuResponseDTO.MenuDetailResponseDTO.builder()
                .menuId(menu.getId())
                .storeId(menu.getStore() != null ? menu.getStore().getId() : null)
                .name(menu.getName())
                .content(menu.getContent())
                .price(menu.getPrice())
                .menuPicture(menu.getMenuPicture())
                .status(menu.getStatus())
                .category(menu.getMenuCategory() != null ? menu.getMenuCategory().getCategory() : null)
                .build();
    }

    public static MenuResponseDTO.MenuListResponseDTO toMenuListResponseDTO(Menu menu) {
        return MenuResponseDTO.MenuListResponseDTO.builder()
                .menuId(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .menuPicture(menu.getMenuPicture())
                .status(menu.getStatus())
                .category(menu.getMenuCategory() != null ? menu.getMenuCategory().getCategory() : "미분류")
                .build();
    }

    public static MenuResponseDTO.MenuTopResponseDTO toMenuTopResponseDTO(Menu menu) {
        return MenuResponseDTO.MenuTopResponseDTO.builder()
                .menuId(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .menuPicture(menu.getMenuPicture())
                .status(menu.getStatus())
                .category(menu.getMenuCategory() != null ? menu.getMenuCategory().getCategory() : "미분류")
                .build();
    }

    public static MenuResponseDTO.MenuTimeTopResponseDTO toMenuTimeTopResponseDTO(Menu menu) {
        return MenuResponseDTO.MenuTimeTopResponseDTO.builder()
                .menuId(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .menuPicture(menu.getMenuPicture())
                .status(menu.getStatus())
                .category(menu.getMenuCategory() != null ? menu.getMenuCategory().getCategory() : "미분류")
                .build();
    }

    public static MenuResponseDTO.MenuRegionTopResponseDTO toMenuRegionTopResponseDTO(Menu menu) {
        return MenuResponseDTO.MenuRegionTopResponseDTO.builder()
                .menuId(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .menuPicture(menu.getMenuPicture())
                .status(menu.getStatus())
                .category(menu.getMenuCategory() != null ? menu.getMenuCategory().getCategory() : "미분류")
                .build();
    }
}
