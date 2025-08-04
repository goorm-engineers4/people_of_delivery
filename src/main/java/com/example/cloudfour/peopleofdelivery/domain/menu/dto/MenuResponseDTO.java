package com.example.cloudfour.peopleofdelivery.domain.menu.dto;

import com.example.cloudfour.peopleofdelivery.domain.menu.enums.MenuStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

public class MenuResponseDTO {

    @Getter
    @Builder
    public static class MenuDetailResponseDTO {
        private UUID menuId;
        private UUID storeId;
        private String storeName;
        private String name;
        private String content;
        private Integer price;
        private String menuPicture;
        private MenuStatus status;
        private String category;
        private java.time.LocalDateTime createdAt;
        private java.time.LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    public static class MenuListResponseDTO {
        private UUID menuId;
        private String name;
        private Integer price;
        private String menuPicture;
        private MenuStatus status;
        private String category;
        private java.time.LocalDateTime createdAt;
    }

    @Getter
    @Builder
    public static class MenuTopResponseDTO {
        private UUID menuId;
        private String name;
        private Integer price;
        private String menuPicture;
        private MenuStatus status;
        private String category;
        private String storeName;
    }

    @Getter
    @Builder
    public static class MenuTimeTopResponseDTO {
        private UUID menuId;
        private String name;
        private Integer price;
        private String menuPicture;
        private MenuStatus status;
        private String category;
        private String storeName;
        private Integer orderCount;
    }

    @Getter
    @Builder
    public static class MenuRegionTopResponseDTO {
        private UUID menuId;
        private String name;
        private Integer price;
        private String menuPicture;
        private MenuStatus status;
        private String category;
        private String storeName;
        private String region;
    }

    @Getter
    @Builder
    public static class MenuStoreListResponseDTO {
        private java.util.List<MenuListResponseDTO> menus;
        private Boolean hasNext;
        private java.time.LocalDateTime nextCursor;
    }
}