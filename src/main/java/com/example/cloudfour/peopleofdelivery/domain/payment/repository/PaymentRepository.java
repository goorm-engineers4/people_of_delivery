package com.example.cloudfour.peopleofdelivery.domain.payment.repository;

import com.example.cloudfour.peopleofdelivery.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
