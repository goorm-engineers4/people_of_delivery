package com.example.cloudfour.peopleofdelivery.domain.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;

import java.time.LocalDateTime;
import java.util.UUID;

public class StoreResponseDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StoreCreateResponseDTO {
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
    public static class StoreUpdateResponseDTO {
    }

    @Getter
    @Builder
    public static class StoreListResponseDTO {
        private UUID storeId;
        private String name;
        private String address;
        private String storePicture;
        private float rating;
        private int reviewCount;

        public static StoreListResponseDTO from(Store store) {
            return StoreListResponseDTO.builder()
                    .storeId(store.getId())
                    .name(store.getName())
                    .address(store.getAddress())
                    .storePicture(store.getStorePicture())
                    .rating(store.getRating() != null ? store.getRating() : 0f)
                    .reviewCount(store.getReviewCount() != null ? store.getReviewCount() : 0)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class StoreUserListResponseDTO {
    }

    @Getter
    @Builder
    public static class StoreDetailResponseDTO {
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
        private float rating;
        private int reviewCount;
        private String category;

        public static StoreDetailResponseDTO from(Store store) {
            return StoreDetailResponseDTO.builder()
                    .storeId(store.getId())
                    .name(store.getName())
                    .address(store.getAddress())
                    .storePicture(store.getStorePicture())
                    .phone(store.getPhone())
                    .content(store.getContent())
                    .minPrice(store.getMinPrice())
                    .deliveryTip(store.getDeliveryTip())
                    .operationHours(store.getOperationHours())
                    .closedDays(store.getClosedDays())
                    .rating(store.getRating() != null ? store.getRating() : 0f)
                    .reviewCount(store.getReviewCount() != null ? store.getReviewCount() : 0)
                    .category(store.getStoreCategory().getCategory())
                    .build();
        }
    }
}
