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

    // 가게별 메뉴 조회 (삭제되지 않은 메뉴만, 생성일 역순, soft delete된 유저 제외)
    @Query("SELECT m FROM Menu m WHERE m.store.id = :storeId AND m.store.isDeleted = false AND m.store.user.isDeleted = false ORDER BY m.createdAt DESC")
    List<Menu> findByStoreIdAndDeletedFalseOrderByCreatedAtDesc(@Param("storeId") UUID storeId);


    // 가게별 메뉴 조회 (커서 기반 페이지네이션, soft delete된 유저 제외)
    @Query("SELECT m FROM Menu m WHERE m.store.id = :storeId AND m.store.isDeleted = false AND m.store.user.isDeleted = false AND m.createdAt < :cursor ORDER BY m.createdAt DESC")
    Slice<Menu> findByStoreIdAndDeletedFalseAndCreatedAtBefore(@Param("storeId") UUID storeId, @Param("cursor") LocalDateTime cursor, Pageable pageable);

    // 메뉴명과 가게ID로 중복 체크 (soft delete된 유저 제외)
    @Query("SELECT COUNT(m) > 0 FROM Menu m WHERE m.name = :name AND m.store.id = :storeId AND m.store.isDeleted = false AND m.store.user.isDeleted = false")
    boolean existsByNameAndStoreId(@Param("name") String name, @Param("storeId") UUID storeId);

    // 인기 메뉴 조회 - 주문 횟수 기준 (삭제되지 않은 가게의 메뉴만, soft delete된 유저 제외)
    @Query("SELECT m FROM Menu m " +
           "LEFT JOIN OrderItem oi ON m.id = oi.menu.id " +
           "WHERE m.store.isDeleted = false AND m.store.user.isDeleted = false " +
           "GROUP BY m.id " +
           "ORDER BY COUNT(oi.id) DESC, m.createdAt DESC")
    List<Menu> findTopMenusByOrderCount(Pageable pageable);

    // 시간대별 인기 메뉴 조회 (삭제되지 않은 가게의 메뉴만, soft delete된 유저 제외)
    @Query("SELECT m FROM Menu m " +
           "LEFT JOIN OrderItem oi ON m.id = oi.menu.id " +
           "LEFT JOIN oi.order o " +
           "WHERE m.store.isDeleted = false AND m.store.user.isDeleted = false " +
           "AND o.createdAt BETWEEN :startTime AND :endTime " +
           "GROUP BY m.id " +
           "ORDER BY COUNT(oi.id) DESC, m.createdAt DESC")
    List<Menu> findTopMenusByTimeRange(@Param("startTime") LocalDateTime startTime,
                                       @Param("endTime") LocalDateTime endTime,
                                       Pageable pageable);

    // 지역별 인기 메뉴 조회 (삭제되지 않은 가게의 메뉴만, soft delete된 유저 제외)
    @Query("SELECT m FROM Menu m " +
           "LEFT JOIN OrderItem oi ON m.id = oi.menu.id " +
           "WHERE m.store.isDeleted = false AND m.store.user.isDeleted = false " +
           "AND m.store.region.si = :si " +
           "AND m.store.region.gu = :gu " +
           "GROUP BY m.id " +
           "ORDER BY COUNT(oi.id) DESC, m.createdAt DESC")
    List<Menu> findTopMenusByRegion(@Param("si") String si, @Param("gu") String gu, Pageable pageable);
}