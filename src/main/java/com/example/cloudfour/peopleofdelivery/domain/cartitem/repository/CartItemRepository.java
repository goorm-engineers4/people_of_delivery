package com.example.cloudfour.peopleofdelivery.domain.cartitem.repository;

import com.example.cloudfour.peopleofdelivery.domain.cartitem.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

}
