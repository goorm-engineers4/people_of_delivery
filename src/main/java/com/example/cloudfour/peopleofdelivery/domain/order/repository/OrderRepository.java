package com.example.cloudfour.peopleofdelivery.domain.order.repository;

import com.example.cloudfour.peopleofdelivery.domain.order.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    @Query("select o from Order o where o.id =:OrderId and o.isDeleted = false")
    Optional<Order> findById(@Param("OrderId") UUID orderId);

    @Query("select o from Order o where o.user.id=:userId and o.user.isDeleted = false and o.isDeleted = false and o.createdAt <:cursor order by o.createdAt desc")
    Slice<Order> findAllByUserId(@Param("userId")  UUID userId, LocalDateTime cursor, Pageable pageable);

    @Query("select o from Order o where o.store.id=:StoreId and o.store.isDeleted = false and o.isDeleted = false and o.createdAt <:cursor order by o.createdAt desc")
    Slice<Order> findAllByStoreId(@Param("StoreId")  UUID storeId, LocalDateTime cursor, Pageable pageable);

    @Query("select count(o) > 0 from Order o where o.id =:OrderId and o.user.id =:UserId and o.user.isDeleted = false and o.isDeleted = false")
    boolean existsByOrderIdAndUserId(@Param("OrderId") UUID orderId, @Param("UserId") UUID userId);
}
