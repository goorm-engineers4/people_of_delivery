package com.example.cloudfour.peopleofdelivery.domain.review.converter;

import com.example.cloudfour.peopleofdelivery.domain.review.dto.ReviewRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.review.dto.ReviewResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.review.entity.Review;

public class ReviewConverter {
    public static Review toReview(ReviewRequestDTO.ReviewCreateRequestDTO reviewCreateRequestDTO){
        return Review.builder()
                .score(reviewCreateRequestDTO.getScore())
                .content(reviewCreateRequestDTO.getContent())
                .pictureUrl(reviewCreateRequestDTO.getPictureUrl())
                .build();
    }

    public static ReviewResponseDTO.ReviewDetailResponseDTO toReviewDetailResponseDTO(Review review){
        return ReviewResponseDTO.ReviewDetailResponseDTO.builder()
                .reviewId(review.getId())
                .storeId(review.getStore().getId())
                .userId(review.getUser().getId())
                .nickname(review.getUser().getNickname())
                .content(review.getContent())
                .pictureUrl(review.getPictureUrl())
                .score(review.getScore())
                .createdAt(review.getCreatedAt())
                .build();
    }

    public static ReviewResponseDTO.ReviewStoreListResponseDTO toReviewStoreListResponseDTO(Review review){
        return ReviewResponseDTO.ReviewStoreListResponseDTO.builder()
                .reviewId(review.getId())
                .userId(review.getUser().getId())
                .content(review.getContent())
                .pictureUrl(review.getPictureUrl())
                .score(review.getScore())
                .createdAt(review.getCreatedAt())
                .createdBy(review.getUser().getId())
                .build();
    }

    public static ReviewResponseDTO.ReviewUserListResponseDTO toReviewUserListResponseDTO(Review review){
        return ReviewResponseDTO.ReviewUserListResponseDTO.builder()
                .reviewId(review.getId())
                .storeName(review.getStore().getName())
                .content(review.getContent())
                .pictureUrl(review.getPictureUrl())
                .score(review.getScore())
                .createdAt(review.getCreatedAt())
                .createdBy(review.getUser().getId())
                .build();
    }

    public static ReviewResponseDTO.ReviewCreateResponseDTO toReviewCreateResponseDTO(Review review){
        return ReviewResponseDTO.ReviewCreateResponseDTO.builder()
                .reviewId(review.getId())
                .storeId(review.getStore().getId())
                .userId(review.getUser().getId())
                .content(review.getContent())
                .pictureUrl(review.getPictureUrl())
                .score(review.getScore())
                .createdAt(review.getCreatedAt())
                .createdBy(review.getUser().getId())
                .build();
    }

    public static ReviewResponseDTO.ReviewUpdateResponseDTO toReviewUpdateResponseDTO(Review review){
        return ReviewResponseDTO.ReviewUpdateResponseDTO.builder()
                .reviewId(review.getId())
                .storeId(review.getStore().getId())
                .userId(review.getUser().getId())
                .content(review.getContent())
                .pictureUrl(review.getPictureUrl())
                .score(review.getScore())
                .updatedAt(review.getUpdatedAt())
                .updatedBy(review.getUser().getId())
                .build();
    }
}

