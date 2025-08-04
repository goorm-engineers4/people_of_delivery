package com.example.cloudfour.peopleofdelivery.domain.payment.entity;

import com.example.cloudfour.peopleofdelivery.domain.payment.enums.PaymentStatus;
import com.example.cloudfour.peopleofdelivery.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "p_payment_history")
public class PaymentHistory extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    private PaymentStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Lob
    private String failedReason;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paymentId", nullable = false)
    private Payment payment;

    public static class PaymentHistoryBuilder{
        private PaymentHistoryBuilder id(UUID id){
            throw new UnsupportedOperationException("id 수동 생성 불가");
        }
    }

    public void setPayment(Payment payment){
        this.payment = payment;
        payment.setPaymentHistory(this);
    }
}
