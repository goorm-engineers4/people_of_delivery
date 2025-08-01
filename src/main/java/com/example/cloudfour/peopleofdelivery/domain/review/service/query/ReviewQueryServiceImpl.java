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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewQueryServiceImpl{
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private static final LocalDateTime first_cursor = LocalDateTime.now().plusDays(1);

    public ReviewResponseDTO.ReviewDetailResponseDTO getReviewById(UUID reviewId, User user) {
        userRepository.findById(user.getId()).orElseThrow(()->new UserException(UserErrorCode.NOT_FOUND));
        Review findReview = reviewRepository.findById(reviewId).orElseThrow(()->new ReviewException(ReviewErrorCode.NOT_FOUND));
        return ReviewConverter.toReviewDetailResponseDTO(findReview);
    }

    public ReviewResponseDTO.ReviewStoreListResponseDTO getReviewListByStore(UUID storeId, LocalDateTime cursor, Integer size, User user) {
        userRepository.findById(user.getId()).orElseThrow(()->new UserException(UserErrorCode.NOT_FOUND));
        storeRepository.findById(storeId).orElseThrow(()->new StoreException(StoreErrorCode.NOT_FOUND));
        if(cursor==null){
            cursor = first_cursor;
        }
        Pageable pageable = PageRequest.of(0,size);

        Slice<Review> findReviews = reviewRepository.findAllByStoreId(storeId,cursor,pageable);
        if(findReviews.isEmpty()){
            throw new ReviewException(ReviewErrorCode.NOT_FOUND);
        }
        List<Review> reviews = findReviews.toList();
        List<ReviewResponseDTO.ReviewStoreResponseDTO> reviewStoreListResponseDTOS = reviews.stream().map(ReviewConverter::toReviewStoreResponseDTO).toList();
        LocalDateTime next_cursor = null;
        if(!findReviews.isEmpty() && findReviews.hasNext()) {
            next_cursor = reviews.getLast().getCreatedAt();
        }

        return ReviewConverter.toReviewStoreListResponseDTO(reviewStoreListResponseDTOS,findReviews.hasNext(),next_cursor);
    }

    public ReviewResponseDTO.ReviewUserListResponseDTO getReviewListByUser(LocalDateTime cursor, Integer size,User user) {
        userRepository.findById(user.getId()).orElseThrow(()->new UserException(UserErrorCode.NOT_FOUND));
        if(cursor==null){
            cursor = first_cursor;
        }
        Pageable pageable = PageRequest.of(0,size);
        Slice<Review> findReviews = reviewRepository.findAllByUserId(user.getId(),cursor,pageable);
        if(findReviews.isEmpty()){
            throw new ReviewException(ReviewErrorCode.NOT_FOUND);
        }
        List<Review> reviews = findReviews.toList();
        List<ReviewResponseDTO.ReviewUserResponseDTO> reviewUserListResponseDTOS = reviews.stream().map(ReviewConverter::toReviewUserResponseDTO).toList();
        LocalDateTime next_cursor = null;
        if(!findReviews.isEmpty() && findReviews.hasNext()) {
            next_cursor = reviews.getLast().getCreatedAt();
        }
        return ReviewConverter.toReviewUserListResponseDTO(reviewUserListResponseDTOS,findReviews.hasNext(),next_cursor);
    }
}
