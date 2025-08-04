package com.example.cloudfour.peopleofdelivery.domain.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TossErrorResponse {

    private String code;         // 오류 코드 (예: INVALID_PAYMENT_STATUS)
    private String message;      // 오류 메시지 (예: 이미 승인된 결제입니다.)
    private String orderId;      // 오류가 발생한 주문 ID
    private String paymentKey;   // 오류가 발생한 결제 키
    private String status;       // 결제 상태 (FAILED 등)
}