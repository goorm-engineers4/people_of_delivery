package com.example.cloudfour.peopleofdelivery.domain.store.repository;

import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

// StoreRepository
public interface StoreRepository extends JpaRepository<Store, UUID> {

    // 사용자 ID로 가게 조회 (soft delete된 유저 제외)
    @Query("SELECT s FROM Store s WHERE s.user.id = :userId AND s.user.isDeleted = false")
    Optional<Store> findByUserId(@Param("userId") UUID userId);
}
    boolean existsByName(String name);

    @Query("SELECT s FROM Store s WHERE s.isDeleted = false AND s.createdAt < :cursor ORDER BY s.createdAt DESC")
    Slice<Store> findAllByCursor(@Param("cursor") LocalDateTime cursor, Pageable pageable);

    @Query("SELECT s FROM Store s WHERE s.isDeleted = false AND s.storeCategory.id = :categoryId AND s.createdAt < :cursor ORDER BY s.createdAt DESC")
    Slice<Store> findAllByCategoryAndCursor(@Param("categoryId") UUID categoryId, @Param("cursor") LocalDateTime cursor, Pageable pageable);
}
