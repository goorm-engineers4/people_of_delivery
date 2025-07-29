package com.example.cloudfour.peopleofdelivery.domain.order.repository;

import com.example.cloudfour.peopleofdelivery.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

}
