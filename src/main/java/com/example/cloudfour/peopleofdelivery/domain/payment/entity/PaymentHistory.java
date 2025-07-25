package com.example.cloudfour.peopleofdelivery.domain.payment.entity;

import com.example.cloudfour.peopleofdelivery.domain.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PaymentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Lob
    private String failedReason;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Payment payment;
}
