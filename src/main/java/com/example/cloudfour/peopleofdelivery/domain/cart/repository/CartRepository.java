package com.example.cloudfour.peopleofdelivery.domain.cart.repository;

import com.example.cloudfour.peopleofdelivery.domain.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    @Query("select c from Cart c where c.id = :cartId and c.user.id = :userId and c.user.isDeleted = false")
    Optional<Cart> findByIdAndUser(@Param("cartId") UUID cartId, @Param("userId") UUID userId);

    @Query("select count(c) > 0 from Cart c where c.user.id = :userId and c.store.id = :storeId and c.user.isDeleted = false")
    boolean existsByUserAndStore(@Param("userId") UUID userId, @Param("storeId") UUID storeId);

    @Query("select count(c) > 0 from Cart c where c.id =:cartId and c.user.id =:userId and c.user.isDeleted = false")
    boolean existsByUserAndCart(@Param("userId") UUID userId, @Param("cartId") UUID cartId);
}