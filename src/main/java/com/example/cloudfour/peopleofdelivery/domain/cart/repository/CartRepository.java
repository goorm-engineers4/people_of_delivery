package com.example.cloudfour.peopleofdelivery.domain.cart.repository;

import com.example.cloudfour.peopleofdelivery.domain.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
}
