package com.example.cloudfour.peopleofdelivery.domain.review.service.query;

import com.example.cloudfour.peopleofdelivery.domain.review.dto.ReviewResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;

import java.util.List;
import java.util.UUID;

public interface ReviewQueryService {
    ReviewResponseDTO.ReviewDetailResponseDTO getReviewById(UUID reviewId, User user);

    List<ReviewResponseDTO.ReviewStoreListResponseDTO> getReviewListByStore(UUID storeId,User user);

    List<ReviewResponseDTO.ReviewUserListResponseDTO> getReviewListByUser(User user);
}
