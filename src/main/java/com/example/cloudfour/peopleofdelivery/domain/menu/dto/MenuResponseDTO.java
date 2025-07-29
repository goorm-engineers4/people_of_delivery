package com.example.cloudfour.peopleofdelivery.domain.menu.dto;

import com.example.cloudfour.peopleofdelivery.domain.menu.enums.MenuStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

public class MenuResponseDTO {

    // 메뉴 생성 응답
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuDetailResponseDTO {
        private UUID menuId;
        private UUID storeId;
        private String name;
        private String content;
        private Integer price;
        private String menuPicture;
        private MenuStatus status;
        private String category;
    }

    // 해당 가게 메뉴 목록 조회 응답
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuListResponseDTO {
        private UUID menuId;
        private String name;
        private Integer price;
        private String menuPicture;
        private MenuStatus status;
        private String category;
    }

    // 인기 메뉴 TOP20 조회 응답
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuTopResponseDTO {
        private UUID menuId;
        private String name;
        private Integer price;
        private String menuPicture;
        private MenuStatus status;
        private String category;
    }

    // 시간대별 인기 메뉴 TOP20 조회 응답
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuTimeTopResponseDTO {
        private UUID menuId;
        private String name;
        private Integer price;
        private String menuPicture;
        private MenuStatus status;
        private String category;
    }

    // 지역별 인기 메뉴 TOP20 조회 응답
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuRegionTopResponseDTO {
        private UUID menuId;
        private String name;
        private Integer price;
        private String menuPicture;
        private MenuStatus status;
        private String category;
    }
}
