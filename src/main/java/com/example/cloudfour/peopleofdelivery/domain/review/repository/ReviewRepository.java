package com.example.cloudfour.peopleofdelivery.domain.review.repository;

import com.example.cloudfour.peopleofdelivery.domain.review.entity.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    @Query("select r from Review r where r.isDeleted = false and r.user.id =:UserId and r.user.isDeleted = false and r.createdAt <:cursor order by r.createdAt desc")
    Slice<Review> findAllByUserId(@Param("UserId") UUID userId, LocalDateTime cursor, Pageable pageable);

    @Query("select r from Review r where r.isDeleted = false and r.store.id =:StoreId and r.store.isDeleted = false and r.createdAt <:cursor order by r.createdAt desc")
    Slice<Review> findAllByStoreId(@Param("StoreId") UUID storeId, LocalDateTime cursor, Pageable pageable);

    @Query("select count(r) > 0 from Review r where r.id =:ReviewId and r.user.id =:UserId and r.user.isDeleted = false")
    boolean existsByReviewIdAndUserId(@Param("ReviewId") UUID reviewId, @Param("UserId") UUID userId);
}
