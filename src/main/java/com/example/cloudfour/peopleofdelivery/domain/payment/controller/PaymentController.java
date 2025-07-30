package com.example.cloudfour.peopleofdelivery.domain.payment.controller;

import com.example.cloudfour.peopleofdelivery.domain.payment.dto.PaymentRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.payment.dto.PaymentResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.payment.service.command.PaymentCommandService;
import com.example.cloudfour.peopleofdelivery.domain.payment.service.query.PaymentQueryService;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
@Tag(name="Payment", description = "결제 API by 김준형")
public class PaymentController {
    private final PaymentQueryService paymentQueryService;
    private final PaymentCommandService paymentCommandService;

    @PostMapping("")
    @Operation(summary = "결제 생성", description = "결제를 생성합니다. 결제 생성에 사용되는 API입니다.")
    public CustomResponse<PaymentResponseDTO.PaymentCreateResponseDTO> createPayment(
            @RequestBody PaymentRequestDTO.PaymentCreateRequestDTO paymentCreateRequestDTO,
            @AuthenticationPrincipal User user
    ){
        PaymentResponseDTO.PaymentCreateResponseDTO payment = paymentCommandService.createPayment(paymentCreateRequestDTO,user);
        return CustomResponse.onSuccess(HttpStatus.CREATED, payment);
    }

    @PostMapping("/verify")
    @Operation(summary = "결제 검증", description = "결제를 검증합니다. 결제 검증에 사용되는 API입니다.")
    public CustomResponse<PaymentResponseDTO.PaymentVerifyResponseDTO> verifyPayment(
            @RequestBody PaymentRequestDTO.PaymentVerifyRequestDTO paymentVerifyRequestDTO,
            @AuthenticationPrincipal User user
    ){
        PaymentResponseDTO.PaymentVerifyResponseDTO payment = paymentCommandService.verifyPayment(paymentVerifyRequestDTO,user);
        return CustomResponse.onSuccess(HttpStatus.CREATED, payment);
    }

    @PatchMapping("/{orderId}/status")
    @Operation(summary = "결제 수정", description = "결제를 수정합니다. 결제 수정에 사용되는 API입니다.")
    public CustomResponse<PaymentResponseDTO.PaymentUpdateResponseDTO> updatePayment(
            @RequestBody PaymentRequestDTO.PaymentUpdateRequestDTO paymentUpdateRequestDTO,
            @PathVariable("orderId") UUID orderId,
            @AuthenticationPrincipal User user
    ){
        PaymentResponseDTO.PaymentUpdateResponseDTO payment = paymentCommandService.updatePayment(paymentUpdateRequestDTO,orderId,user);
        return CustomResponse.onSuccess(HttpStatus.OK, payment);
    }

    @PatchMapping("/{orderId}/canceled")
    @Operation(summary = "결제 삭제", description = "결제를 삭제합니다. 결제 수정에 사용되는 API입니다.")
    public CustomResponse<PaymentResponseDTO.PaymentDeleteResponseDTO> deletePayment(
            @RequestBody PaymentRequestDTO.PaymentDeleteRequestDTO paymentDeleteRequestDTO,
            @PathVariable("orderId") UUID orderId,
            @AuthenticationPrincipal User user
    ){
        PaymentResponseDTO.PaymentDeleteResponseDTO payment = paymentCommandService.deletePayment(paymentDeleteRequestDTO,orderId,user);
        return CustomResponse.onSuccess(HttpStatus.OK, payment);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "결제 상세 조회", description = "결제를 상세 조회합니다. 결제 상세 조회에 사용되는 API입니다.")
    public CustomResponse<PaymentResponseDTO.PaymentDetailResponseDTO> getPayment(
            @PathVariable("orderId") UUID orderId,
            @AuthenticationPrincipal User user
    ){
        PaymentResponseDTO.PaymentDetailResponseDTO payment = paymentQueryService.getDetailPayment(orderId,user);
        return CustomResponse.onSuccess(HttpStatus.OK, payment);
    }

    @GetMapping("/{storeId}")
    @Operation(summary = "가게 결제 이력 조회", description = " 가게 결제 이력을 조회합니다. 가게 결제 이력 조회에 사용되는 API입니다.")
    public CustomResponse<PaymentResponseDTO.PaymentStoreListResponseDTO> getStorePayment(
            @PathVariable("storeId") UUID storeId,
            @AuthenticationPrincipal User user
    ){
        PaymentResponseDTO.PaymentStoreListResponseDTO payment = paymentQueryService.getStoreListPayment(storeId,user);
        return CustomResponse.onSuccess(HttpStatus.OK, payment);
    }

    @GetMapping("/me")
    @Operation(summary = "내 결제 이력 조회", description = "내 결제 이력을 조회합니다. 내 결제 이력 조회에 사용되는 API입니다.")
    public CustomResponse<PaymentResponseDTO.PaymentUserListResponseDTO> getUserPayment(
            @AuthenticationPrincipal User user
    ){
        PaymentResponseDTO.PaymentUserListResponseDTO payment = paymentQueryService.getUserListPayment(user);
        return CustomResponse.onSuccess(HttpStatus.OK, payment);
    }

    @GetMapping("/{storeId}/summary")
    @Operation(summary = "가게 매출 요약", description = "가게 매출 요약을 조회합니다. 가게 매출 요약 조회에 사용되는 API입니다.")
    public CustomResponse<PaymentResponseDTO.PaymentStoreSummaryResponseDTO> verifyPayment(
            @PathVariable("storeId") UUID storeId,
            @AuthenticationPrincipal User user
    ) {
        PaymentResponseDTO.PaymentStoreSummaryResponseDTO payment = paymentQueryService.getStoreSummaryPayment(storeId, user);
        return CustomResponse.onSuccess(HttpStatus.OK, payment);
    }
}
