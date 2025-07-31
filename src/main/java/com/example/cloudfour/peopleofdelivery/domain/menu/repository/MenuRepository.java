package com.example.cloudfour.peopleofdelivery.domain.menu.repository;

import com.example.cloudfour.peopleofdelivery.domain.menu.entity.Menu;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MenuRepository extends JpaRepository<Menu, UUID> {

    // 가게별 메뉴 조회 (삭제되지 않은 메뉴만, 생성일 역순)
    @Query("SELECT m FROM Menu m WHERE m.store.id = :storeId AND m.store.isDeleted = false ORDER BY m.createdAt DESC")
    List<Menu> findByStoreIdAndDeletedFalseOrderByCreatedAtDesc(@Param("storeId") UUID storeId);

    // 가게별 메뉴 조회 (페이지네이션 지원)
    @Query("SELECT m FROM Menu m WHERE m.store.id = :storeId AND m.store.isDeleted = false ORDER BY m.createdAt DESC")
    List<Menu> findByStoreIdAndDeletedFalseOrderByCreatedAtDesc(@Param("storeId") UUID storeId, Pageable pageable);

    // 가게별 메뉴 조회 (커서 기반 페이지네이션)
    @Query("SELECT m FROM Menu m WHERE m.store.id = :storeId AND m.store.isDeleted = false AND m.createdAt < :cursor ORDER BY m.createdAt DESC")
    Slice<Menu> findByStoreIdAndDeletedFalseAndCreatedAtBefore(@Param("storeId") UUID storeId, @Param("cursor") LocalDateTime cursor, Pageable pageable);

    // 메뉴명과 가게ID로 중복 체크
    @Query("SELECT COUNT(m) > 0 FROM Menu m WHERE m.name = :name AND m.store.id = :storeId AND m.store.isDeleted = false")
    boolean existsByNameAndStoreId(@Param("name") String name, @Param("storeId") UUID storeId);

    // 인기 메뉴 조회 - 주문 횟수 기준 (삭제되지 않은 가게의 메뉴만)
    @Query("SELECT m FROM Menu m WHERE m.store.isDeleted = false ORDER BY m.createdAt DESC")
    List<Menu> findTopMenusByOrderCount(Pageable pageable);

    // 시간대별 인기 메뉴 조회 (삭제되지 않은 가게의 메뉴만)
    @Query("SELECT m FROM Menu m WHERE m.createdAt BETWEEN :startTime AND :endTime AND m.store.isDeleted = false ORDER BY m.createdAt DESC")
    List<Menu> findTopMenusByTimeRange(@Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime,
                                     Pageable pageable);

    // 지역별 인기 메뉴 조회 (삭제되지 않은 가게의 메뉴만)
    @Query("SELECT m FROM Menu m WHERE m.store.isDeleted = false ORDER BY m.createdAt DESC")
    List<Menu> findTopMenusByRegion(@Param("region") String region, Pageable pageable);
}
