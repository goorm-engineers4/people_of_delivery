package com.example.cloudfour.peopleofdelivery.domain.menu.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

public class MenuOptionResponseDTO {
    @Getter
    @Builder
    public static class MenuOptionListResponseDTO{
        UUID menuId;
        String optionName;
        Integer additionalPrice;
    }

    @Getter
    @Builder
    public static class MenuOptionDetailResponseDTO {
        private UUID menuOptionId;
        private UUID menuId;
        private String menuName;
        private String storeName;
        private String optionName;
        private Integer additionalPrice;
        private java.time.LocalDateTime createdAt;
        private java.time.LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    public static class MenuOptionsByMenuResponseDTO {
        private UUID menuId;
        private String menuName;
        private List<MenuOptionSimpleResponseDTO> options;
    }

    @Getter
    @Builder
    public static class MenuOptionSimpleResponseDTO {
        private UUID menuOptionId;
        private String optionName;
        private Integer additionalPrice;
    }
}
