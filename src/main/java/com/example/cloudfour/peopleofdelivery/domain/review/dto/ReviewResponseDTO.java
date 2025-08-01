package com.example.cloudfour.peopleofdelivery.domain.review.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ReviewResponseDTO {
    @Getter
    @Builder
    public static class ReviewCreateResponseDTO{
        UUID reviewId;
        UUID storeId;
        UUID userId;
        Integer score;
        String content;
        String pictureUrl;
        LocalDateTime createdAt;
        UUID createdBy;
    }

    @Getter
    @Builder
    public static class ReviewUpdateResponseDTO{
        UUID reviewId;
        UUID storeId;
        UUID userId;
        Integer score;
        String content;
        String pictureUrl;
        LocalDateTime updatedAt;
        UUID updatedBy;
    }

    @Getter
    @Builder
    public static class ReviewStoreResponseDTO{
        UUID reviewId;
        UUID userId;
        Integer score;
        String content;
        String pictureUrl;
        LocalDateTime createdAt;
        UUID createdBy;
    }

    @Getter
    @Builder
    public static class ReviewStoreListResponseDTO{
        List<ReviewResponseDTO.ReviewStoreResponseDTO> reviews;
        private boolean hasNext;
        private LocalDateTime cursor;
    }

    @Getter
    @Builder
    public static class ReviewUserResponseDTO{
        UUID reviewId;
        String storeName;
        Integer score;
        String content;
        String pictureUrl;
        LocalDateTime createdAt;
        UUID createdBy;
    }

    @Getter
    @Builder
    public static class ReviewUserListResponseDTO{
        List<ReviewResponseDTO.ReviewUserResponseDTO> reviews;
        private boolean hasNext;
        private LocalDateTime cursor;
    }

    @Getter
    @Builder
    public static class ReviewDetailResponseDTO{
        UUID reviewId;
        UUID storeId;
        UUID userId;
        String nickname;
        Integer score;
        String content;
        String pictureUrl;
        LocalDateTime createdAt;
    }
}
