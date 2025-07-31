package com.example.cloudfour.peopleofdelivery.domain.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

public class StoreResponseDTO {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StoreCreateResponseDTO{
        private UUID storeId;
        private String name;
        private String address;
        private String storePicture;
        private String phone;
        private String content;
        private Integer minPrice;
        private Integer deliveryTip;
        private String operationHours;
        private String closedDays;
        private String category;
        private LocalDateTime createdAt;
        private UUID createdBy;
    }

    @Getter
    @Builder
    public static class StoreUpdateResponseDTO{
    }


    @Getter
    @Builder
    public static class StoreListResponseDTO{
    }

    @Getter
    @Builder
    public static class StoreUserListResponseDTO{
    }

    @Getter
    @Builder
    public static class StoreDetailResponseDTO{

    }
}

