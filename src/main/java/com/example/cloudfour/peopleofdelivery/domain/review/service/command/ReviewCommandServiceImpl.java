package com.example.cloudfour.peopleofdelivery.domain.review.service.command;

import com.example.cloudfour.peopleofdelivery.domain.review.converter.ReviewConverter;
import com.example.cloudfour.peopleofdelivery.domain.review.dto.ReviewRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.review.dto.ReviewResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.review.entity.Review;
import com.example.cloudfour.peopleofdelivery.domain.review.exception.ReviewErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.review.exception.ReviewException;
import com.example.cloudfour.peopleofdelivery.domain.review.repository.ReviewRepository;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.store.exception.StoreErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.store.exception.StoreException;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.domain.user.exception.UserErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.user.exception.UserException;
import com.example.cloudfour.peopleofdelivery.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewCommandServiceImpl{
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;

    public ReviewResponseDTO.ReviewCreateResponseDTO createReview(ReviewRequestDTO.ReviewCreateRequestDTO reviewCreateRequestDTO, User user) {
        userRepository.findById(user.getId()).orElseThrow(()->new UserException(UserErrorCode.NOT_FOUND));
        Store findStore = storeRepository.findById(reviewCreateRequestDTO.getStoreId()).orElseThrow(()->new StoreException(StoreErrorCode.NOT_FOUND));
        Review review = ReviewConverter.toReview(reviewCreateRequestDTO);
        review.setUser(user);
        review.setStore(findStore);
        reviewRepository.save(review);
        return ReviewConverter.toReviewCreateResponseDTO(review);
    }

    public void deleteReview(UUID reviewId, User user) {
        userRepository.findById(user.getId()).orElseThrow(()->new UserException(UserErrorCode.NOT_FOUND));
        Review findReview = reviewRepository.findById(reviewId).orElseThrow(()->new ReviewException(ReviewErrorCode.NOT_FOUND));
        if(!reviewRepository.existsByReviewIdAndUserId(reviewId, user.getId())) {
            throw new ReviewException(ReviewErrorCode.UNAUTHORIZED_ACCESS);
        }
        findReview.softDelete();
    }

    public ReviewResponseDTO.ReviewUpdateResponseDTO updateReview(ReviewRequestDTO.ReviewUpdateRequestDTO reviewUpdateRequestDTO, UUID reviewId, User user) {
        User finduser = userRepository.findById(user.getId()).orElseThrow(()->new UserException(UserErrorCode.NOT_FOUND));
        Store findStore = storeRepository.findById(reviewUpdateRequestDTO.getStoreId()).orElseThrow(()->new StoreException(StoreErrorCode.NOT_FOUND));
        Review findReview = reviewRepository.findById(reviewId).orElseThrow(()->new ReviewException(ReviewErrorCode.NOT_FOUND));
        findReview.update(reviewUpdateRequestDTO);
        findReview.setStore(findStore);
        return ReviewConverter.toReviewUpdateResponseDTO(findReview);
    }
}
