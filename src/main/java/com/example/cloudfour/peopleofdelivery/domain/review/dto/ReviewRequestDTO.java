package com.example.cloudfour.peopleofdelivery.domain.review.dto;

import lombok.Builder;
import lombok.Getter;

public class ReviewRequestDTO {
    @Getter
    @Builder
    public static class ReviewCreateRequestDTO{
        Integer score;
        String content;
        String pictureUrl;
    }

    @Getter
    @Builder
    public static class ReviewUpdateRequestDTO{
        Integer score;
        String content;
        String pictureUrl;
    }
}
