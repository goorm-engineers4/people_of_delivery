package com.example.cloudfour.peopleofdelivery.domain.order.repository;

import com.example.cloudfour.peopleofdelivery.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findById(UUID orderId);
}
