package com.example.cloudfour.peopleofdelivery.domain.review.controller;

import com.example.cloudfour.peopleofdelivery.domain.review.dto.ReviewRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.review.dto.ReviewResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.review.service.command.ReviewCommandService;
import com.example.cloudfour.peopleofdelivery.domain.review.service.query.ReviewQueryService;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
@Tag(name = "Review", description = "리뷰 API by 김준형")
public class ReviewController {
    private final ReviewCommandService reviewCommandService;
    private final ReviewQueryService reviewQueryService;

    @PostMapping("/{storeId}")
    @Operation(summary = "리뷰 생성", description = "리뷰를 생성합니다. 리뷰 생성에 사용되는 API입니다.")
    public CustomResponse<ReviewResponseDTO.ReviewCreateResponseDTO> createReview(
        @RequestBody ReviewRequestDTO.ReviewCreateRequestDTO reviewCreateRequestDTO,
        @PathVariable("storeId") UUID storeId,
        @AuthenticationPrincipal User user
    ){
        ReviewResponseDTO.ReviewCreateResponseDTO review = reviewCommandService.createReview(reviewCreateRequestDTO,storeId,user);
        return CustomResponse.onSuccess(HttpStatus.CREATED, review);
    }

    @PatchMapping("/{storeId}/{reviewId}")
    @Operation(summary = "리뷰 수정", description = "리뷰를 수정합니다. 리뷰 수정에 사용되는 API입니다.")
    public CustomResponse<ReviewResponseDTO.ReviewUpdateResponseDTO> updateReview(
            @RequestBody ReviewRequestDTO.ReviewUpdateRequestDTO reviewUpdateRequestDTO,
            @PathVariable("storeId") UUID storeId,
            @PathVariable("reviewId") UUID reviewId,
            @AuthenticationPrincipal User user
    ){
        ReviewResponseDTO.ReviewUpdateResponseDTO review = reviewCommandService.updateReview(reviewUpdateRequestDTO,storeId,reviewId,user);
        return CustomResponse.onSuccess(HttpStatus.OK, review);
    }

    @PatchMapping("/{reviewId}/canceled")
    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제합니다. 리뷰 삭제에 사용되는 API입니다.")
    public CustomResponse<String> deleteReview(
            @RequestBody ReviewRequestDTO.ReviewUpdateRequestDTO reviewRequestDTO,
            @PathVariable("reviewId") UUID reviewId,
            @AuthenticationPrincipal User user
    ){
        reviewCommandService.deleteReview(reviewId,user);
        return CustomResponse.onSuccess(HttpStatus.OK, "리뷰 삭제 완료");
    }

    @GetMapping("/{reviewId}/detail")
    @Operation(summary = "리뷰 상세 조회", description = "리뷰를 상세 조회합니다. 리뷰 상세 조회에 사용되는 API입니다.")
    public CustomResponse<ReviewResponseDTO.ReviewDetailResponseDTO> getReview(
            @PathVariable("reviewId") UUID reviewId,
            @AuthenticationPrincipal User user
    ){
        ReviewResponseDTO.ReviewDetailResponseDTO review = reviewQueryService.getReviewById(reviewId,user);
        return CustomResponse.onSuccess(HttpStatus.OK, review);
    }

    @GetMapping("")
    @Operation(summary = "유저 리뷰 조회", description = "사용자가 작성한 리뷰를 조회합니다. 사용자 리뷰 조회에 사용되는 API입니다.")
    public CustomResponse<List<ReviewResponseDTO.ReviewUserListResponseDTO>> getUserReviews(
            @AuthenticationPrincipal User user
    ){
        List<ReviewResponseDTO.ReviewUserListResponseDTO> review = reviewQueryService.getReviewListByUser(user);
        return CustomResponse.onSuccess(HttpStatus.OK, review);
    }

    @GetMapping("{storeId}")
    @Operation(summary = "가게 리뷰 조회", description = "가게에 있는 리뷰를 조회합니다. 가게 리뷰 조회에 사용되는 API입니다.")
    public CustomResponse<List<ReviewResponseDTO.ReviewStoreListResponseDTO>> getStoreReviews(
            @PathVariable("storeId") UUID storeId,
            @AuthenticationPrincipal User user
    ){
        List<ReviewResponseDTO.ReviewStoreListResponseDTO> review = reviewQueryService.getReviewListByStore(storeId,user);
        return CustomResponse.onSuccess(HttpStatus.OK, review);
    }
}
