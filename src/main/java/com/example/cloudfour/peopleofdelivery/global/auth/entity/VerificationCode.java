package com.example.cloudfour.peopleofdelivery.global.auth.entity;

import com.example.cloudfour.peopleofdelivery.domain.user.enums.VerificationPurpose;
import com.example.cloudfour.peopleofdelivery.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class VerificationCode {
    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationPurpose purpose;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    public boolean isExpired() { return LocalDateTime.now().isAfter(expiryDate); }
}