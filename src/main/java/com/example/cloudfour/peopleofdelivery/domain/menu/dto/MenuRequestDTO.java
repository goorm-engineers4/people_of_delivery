package com.example.cloudfour.peopleofdelivery.domain.menu.dto;

import com.example.cloudfour.peopleofdelivery.domain.menu.enums.MenuStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

public class MenuRequestDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuCreateRequestDTO {
        private String name;
        private String content;
        private Integer price;
        private String menuPicture;
        private MenuStatus status;
        private String category;
        private List<MenuOptionCreateRequestDTO> menuOptions;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuUpdateRequestDTO {
        private String name;
        private String content;
        private Integer price;
        private String menuPicture;
        private MenuStatus status;
        private String category;
        private List<MenuOptionCreateRequestDTO> menuOptions;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuOptionCreateRequestDTO {
        private String optionName;
        private Integer additionalPrice;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuOptionUpdateRequestDTO {
        private UUID optionId;
        private String optionName;
        private Integer additionalPrice;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuOptionStandaloneCreateRequestDTO {
        private UUID menuId;
        private String optionName;
        private Integer additionalPrice;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuOptionStandaloneUpdateRequestDTO {
        private String optionName;
        private Integer additionalPrice;
    }
}
