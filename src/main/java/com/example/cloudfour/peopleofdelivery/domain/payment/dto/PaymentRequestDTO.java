package com.example.cloudfour.peopleofdelivery.domain.payment.dto;

import com.example.cloudfour.peopleofdelivery.domain.payment.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

public class PaymentRequestDTO {
    @Getter
    @Builder
    public static class PaymentCreateRequestDTO{
        UUID orderId;
        Integer totalPrice;
    }

    @Getter
    @Builder
    public static class PaymentVerifyRequestDTO{
        String paymentKey;
        String orderId; // tossOrderId
        Integer amount;
    }

    @Getter
    @Builder
    public static class PaymentUpdateRequestDTO{
        UUID paymentHistoryId;
        PaymentStatus paymentStatus;
        UUID updatedBy;
    }

    @Getter
    @Builder
    public static class PaymentDeleteRequestDTO{
        String cancelReason;
        UUID updatedBy;
    }

}
