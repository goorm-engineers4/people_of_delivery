package com.example.cloudfour.peopleofdelivery.domain.review.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

public class ReviewRequestDTO {
    @Getter
    @Builder
    public static class ReviewCreateRequestDTO{
        UUID storeId;
        Integer score;
        String content;
        String pictureUrl;
    }

    @Getter
    @Builder
    public static class ReviewUpdateRequestDTO{
        UUID storeId;
        Integer score;
        String content;
        String pictureUrl;
    }
}
