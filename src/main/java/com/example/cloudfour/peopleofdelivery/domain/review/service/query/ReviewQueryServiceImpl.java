package com.example.cloudfour.peopleofdelivery.domain.review.service.query;

import com.example.cloudfour.peopleofdelivery.domain.review.converter.ReviewConverter;
import com.example.cloudfour.peopleofdelivery.domain.review.dto.ReviewResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.review.entity.Review;
import com.example.cloudfour.peopleofdelivery.domain.review.exception.ReviewErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.review.exception.ReviewException;
import com.example.cloudfour.peopleofdelivery.domain.review.repository.ReviewRepository;
import com.example.cloudfour.peopleofdelivery.domain.store.exception.StoreErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.store.exception.StoreException;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.domain.user.exception.UserErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.user.exception.UserException;
import com.example.cloudfour.peopleofdelivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewQueryServiceImpl{
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    public ReviewResponseDTO.ReviewDetailResponseDTO getReviewById(UUID reviewId, User user) {
        userRepository.findById(user.getId()).orElseThrow(()->new UserException(UserErrorCode.NOT_FOUND));
        Review findReview = reviewRepository.findById(reviewId).orElseThrow(()->new ReviewException(ReviewErrorCode.NOT_FOUND));
        return ReviewConverter.toReviewDetailResponseDTO(findReview);
    }

    public List<ReviewResponseDTO.ReviewStoreListResponseDTO> getReviewListByStore(UUID storeId, User user) {
        userRepository.findById(user.getId()).orElseThrow(()->new UserException(UserErrorCode.NOT_FOUND));
        storeRepository.findById(storeId).orElseThrow(()->new StoreException(StoreErrorCode.NOT_FOUND));
        List<Review> findReviews = reviewRepository.findAllByStoreId(storeId);
        return findReviews.stream().map(ReviewConverter::toReviewStoreListResponseDTO).toList();
    }

    public List<ReviewResponseDTO.ReviewUserListResponseDTO> getReviewListByUser(User user) {
        userRepository.findById(user.getId()).orElseThrow(()->new UserException(UserErrorCode.NOT_FOUND));
        List<Review> findReviews = reviewRepository.findAllByUserId(user.getId());
        return findReviews.stream().map(ReviewConverter::toReviewUserListResponseDTO).toList();
    }
}
