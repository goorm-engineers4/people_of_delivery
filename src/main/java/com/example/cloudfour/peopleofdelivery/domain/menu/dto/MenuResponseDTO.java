package com.example.cloudfour.peopleofdelivery.domain.menu.dto;

import com.example.cloudfour.peopleofdelivery.domain.menu.enums.MenuStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

// MenuResponseDTO: 서버 -> 클라이언트로 보내는 데이터
public class MenuResponseDTO {

    // 메뉴 생성 응답
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

    // 해당 가게 메뉴 목록 조회 응답
    @Getter
    @Builder
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
    public static class MenuTopResponseDTO {
        private UUID menuId;
        private String name;
        private Integer price;
        private String menuPicture;
        private MenuStatus status;
        private String category;
        private String storeName;
    }

    // 시간대별 인기 메뉴 TOP20 조회 응답
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

    // 지역별 인기 메뉴 TOP20 조회 응답
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

    // 커서 기반 페이지네이션을 위한 가게별 메뉴 목록 응답
    @Getter
    @Builder
    public static class MenuStoreListResponseDTO {
        private java.util.List<MenuListResponseDTO> menus;
        private Boolean hasNext;
        private java.time.LocalDateTime nextCursor;
    }
}