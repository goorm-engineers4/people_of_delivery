package com.example.cloudfour.peopleofdelivery.domain.payment.entity;

import com.example.cloudfour.peopleofdelivery.domain.order.entity.Order;
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
@Table(name = "p_payment")
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String paymentKey;

    @Column(nullable = false)
    private String tossOrderId;

    @Column(nullable = false)
    private Integer totalPrice;

    @Column(nullable = false)
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Lob
    private String failedReason;

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private PaymentHistory paymentHistory;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="orderId", nullable = false)
    private Order order;

    public void setPaymentStatus(PaymentStatus paymentStatus) {this.paymentStatus = paymentStatus;}

    public void setFailedReason(String s) { this.failedReason = s;}

    public static class PaymentBuilder{
        private PaymentBuilder id(UUID id){
            throw new UnsupportedOperationException("id 수동 생성 불가");
        }
    }

    public void setPaymentHistory(PaymentHistory paymentHistory){
        this.paymentHistory = paymentHistory;
    }

    public PaymentHistory addHistory(PaymentStatus previousStatus, PaymentStatus currentStatus, String changeReason) {
        PaymentHistory history = PaymentHistory.builder()
                .previousStatus(previousStatus)
                .paymentStatus(currentStatus)
                .failedReason(this.getFailedReason())
                .build();

        history.setPayment(this);
        this.setPaymentHistory(history);
        return history;
    }

    public void markAsFailed(String message) {
        this.paymentStatus = PaymentStatus.FAILED;
        this.failedReason = message;
    }

    public void setOrder(Order order){
        this.order = order;
        order.setPayment(this);
    }
}


