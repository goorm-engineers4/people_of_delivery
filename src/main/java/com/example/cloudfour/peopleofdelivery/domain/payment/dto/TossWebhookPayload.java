package com.example.cloudfour.peopleofdelivery.domain.payment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TossWebhookPayload {
    private String eventType;   // PAYMENT_APPROVED, PAYMENT_CANCELED 등
    private String paymentKey;
    private String orderId;
    private String status;      // APPROVED, CANCELED 등
    private Integer totalAmount;
    private String approvedAt;
}