package com.example.cloudfour.peopleofdelivery.domain.payment.dto;

import com.example.cloudfour.peopleofdelivery.domain.payment.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentResponseDTO {
    @Getter
    @Builder
    public static class PaymentCreateResponseDTO{
        UUID payId;
        UUID orderId;
        Integer totalPrice;
        String orderName;
    }

    @Getter
    @Builder
    public static class PaymentVerifyResponseDTO{
        UUID paymentId;
        UUID orderId;
        PaymentStatus paymentStatus;
        Integer amount;
        String method;
        LocalDateTime createdAt;
        UUID createdBy;
    }

    @Getter
    @Builder
    public static class PaymentUpdateResponseDTO{
        UUID paymentHistoryId;
        PaymentStatus paymentStatus;
        UUID updatedBy;
    }

    @Getter
    @Builder
    public static class PaymentDetailResponseDTO{
        UUID payId;
        UUID orderId;
        Integer totalPrice;
        String paymentMethod;
        PaymentStatus paymentStatus;
        LocalDateTime createdAt;
        UUID createdBy;
        LocalDateTime updatedAt;
        UUID updatedBy;
    }

    @Getter
    @Builder
    public static class PaymentDeleteResponseDTO{
        UUID orderId;
        PaymentStatus paymentStatus;
        LocalDateTime updatedAt;
        UUID updatedBy;
    }

    @Getter
    @Builder
    public static class PaymentUserListResponseDTO{
        UUID paymentId;
        UUID orderId;
        UUID storeId;
        String storeName;
        Integer amount;
        String paymentMethod;
        PaymentStatus paymentStatus;
        LocalDateTime createdAt;
        LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    public static class PaymentStoreListResponseDTO{
        UUID paymentId;
        UUID orderId;
        Integer amount;
        String paymentMethod;
        PaymentStatus paymentStatus;
        LocalDateTime createdAt;
        LocalDateTime updatedAt;
        UUID userid;
        String username;
    }

    @Getter
    @Builder
    public static class PaymentStoreSummaryResponseDTO{
        Integer totalPrice;
        Integer refundPrice;
        Integer paymentAmount;
        Integer refundCount;
    }

}
