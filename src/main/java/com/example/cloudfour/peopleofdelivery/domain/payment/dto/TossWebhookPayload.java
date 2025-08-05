package com.example.cloudfour.peopleofdelivery.domain.payment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TossWebhookPayload {
    private String eventType;
    private String paymentKey;
    private String orderId;
    private String status;
    private Integer totalAmount;
    private String approvedAt;
}