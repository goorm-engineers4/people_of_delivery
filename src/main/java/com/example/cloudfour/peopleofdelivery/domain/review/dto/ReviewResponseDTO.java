package com.example.cloudfour.peopleofdelivery.domain.review.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
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
    public static class ReviewStoreListResponseDTO{
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
    public static class ReviewUserListResponseDTO{
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
