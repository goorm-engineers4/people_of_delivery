package com.example.cloudfour.peopleofdelivery.domain.payment.enums;

public enum PaymentStatus {
    READY,         // 결제 요청 전 or 생성만 된 상태
    IN_PROGRESS,   // Toss 결제창으로 넘어간 상태
    APPROVED,      // 결제 성공
    CANCELED,      // 결제 취소
    FAILED,        // 결제 실패
    PARTIAL_REFUNDED, // 부분 환불됨
    REFUNDED        // 전체 환불 완료
}
