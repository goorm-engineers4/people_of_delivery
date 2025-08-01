package com.example.cloudfour.peopleofdelivery.domain.store.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

public class StoreRequestDTO {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StoreCreateRequestDTO{
        private String name;
        private String address;
        private String storePicture;
        private String phone;
        private String content;
        private Integer minPrice;
        private Integer deliveryTip;
        private String operationHours;
        private String closedDays;
        private UUID regionId;
        private UUID storeCategoryId;
    }

    @Getter
    @Builder
    public static class StoreUpdateRequestDTO{
        private String name;
        private String address;
        private String category;
        private int likeCount;
        private float rating;
        private int reviewCount;
    }
}


