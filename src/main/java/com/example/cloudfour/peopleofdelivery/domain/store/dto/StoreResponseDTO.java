package com.example.cloudfour.peopleofdelivery.domain.store.dto;

import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
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
        private LocalDateTime updatedAt;

        public static StoreUpdateResponseDTO from(Store store) {
            return StoreUpdateResponseDTO.builder()
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
                    .category(store.getStoreCategory().getCategory())
                    .updatedAt(store.getUpdatedAt())
                    .build();
        }
    }

    // StoreListResponseDTO에 createdAt 추가
    @Getter
    @Builder
    public static class StoreListResponseDTO {
        private UUID storeId;
        private String name;
        private String address;
        private String storePicture;
        private float rating;
        private int reviewCount;
        private LocalDateTime createdAt;

        public static StoreListResponseDTO from(Store store) {
            return StoreListResponseDTO.builder()
                    .storeId(store.getId())
                    .name(store.getName())
                    .address(store.getAddress())
                    .storePicture(store.getStorePicture())
                    .rating(store.getRating() != null ? store.getRating() : 0f)
                    .reviewCount(store.getReviewCount() != null ? store.getReviewCount() : 0)
                    .createdAt(store.getCreatedAt())
                    .build();
        }
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

    @Getter
    @Builder
    public static class StoreCursorListResponseDTO {
        private List<StoreListResponseDTO> storeList;
        private LocalDateTime nextCursor;

        public static StoreCursorListResponseDTO of(List<StoreListResponseDTO> storeList, LocalDateTime nextCursor) {
            return StoreCursorListResponseDTO.builder()
                    .storeList(storeList)
                    .nextCursor(nextCursor)
                    .build();
        }
    }
}
