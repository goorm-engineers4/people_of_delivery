package com.example.cloudfour.peopleofdelivery.domain.store.repository;

import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {

    // 사용자 ID로 가게 조회
    @Query("SELECT s FROM Store s WHERE s.user.id = :userId")
    Optional<Store> findByUserId(@Param("userId") UUID userId);
}