package com.example.cloudfour.peopleofdelivery.domain.menu.converter;

import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.Menu;

public class MenuConverter {

    // DTO → Entity 변환
    public static Menu toMenu(MenuRequestDTO.MenuCreateRequestDTO requestDTO) {
        return Menu.builder()
                .name(requestDTO.getName())
                .content(requestDTO.getContent())
                .price(requestDTO.getPrice())
                .menuPicture(requestDTO.getMenuPicture())
                .status(requestDTO.getStatus())
                .build();
    }

    // Entity → 상세응답 DTO 변환
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

    // Entity → 목록응답 DTO 변환
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

    // Entity → 인기 메뉴 TOP20 응답 DTO 변환
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

    // Entity → 시간대별 인기 메뉴 TOP20 응답 DTO 변환
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
    // Entity → 지역별 인기 메뉴 TOP20 응답 DTO 변환
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
