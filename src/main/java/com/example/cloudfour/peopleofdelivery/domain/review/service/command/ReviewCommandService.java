package com.example.cloudfour.peopleofdelivery.domain.review.service.command;

import com.example.cloudfour.peopleofdelivery.domain.review.dto.ReviewRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.review.dto.ReviewResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;

import java.util.UUID;

public interface ReviewCommandService {
    ReviewResponseDTO.ReviewCreateResponseDTO createReview(ReviewRequestDTO.ReviewCreateRequestDTO reviewCreateRequestDTO, UUID storeId, User user);

    ReviewResponseDTO.ReviewUpdateResponseDTO updateReview(ReviewRequestDTO.ReviewUpdateRequestDTO reviewUpdateRequestDTO, UUID storeId, UUID reviewId, User user);

    void deleteReview(UUID reviewId, User user);
}
