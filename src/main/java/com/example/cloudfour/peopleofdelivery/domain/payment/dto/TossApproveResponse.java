package com.example.cloudfour.peopleofdelivery.domain.payment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class TossApproveResponse {

    private String paymentKey;       // 결제 키 (고유 식별자)
    private String orderId;          // 주문 ID (상점에서 전달한 값)
    private String status;           // 결제 상태 (DONE, CANCELED, FAILED 등)
    private String requestedAt;      // 결제 요청 시각
    private String approvedAt;       // 결제 승인 시각
    private Integer totalAmount;     // 결제 총액
    private String method;           // 결제 수단 (카드, 가상계좌 등)
    private String version;          // API 버전 (v1 등)

    // Optional: 카드 결제인 경우
    private Card card;

    @Getter
    @Builder
    public static class Card {
        private String company;          // 카드사명 (예: 삼성)
        private String number;           // 마스킹된 카드 번호
        private int installmentPlanMonths; // 할부 개월 수
        private String approveNo;        // 승인 번호
        private boolean isInterestFree;  // 무이자 여부
        private String cardType;         // 신용/체크 구분
        private String ownerType;        // 개인/법인 구분
    }
}
