package com.example.cloudfour.peopleofdelivery.domain.menu.repository;

import com.example.cloudfour.peopleofdelivery.domain.menu.entity.Menu;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MenuRepository extends JpaRepository<Menu, UUID> {

    // 가게별 메뉴 조회 (삭제되지 않은 메뉴만, 생성일 역순)
    @Query("SELECT m FROM Menu m WHERE m.store.id = :storeId ORDER BY m.createdAt DESC")
    List<Menu> findByStoreIdAndDeletedFalseOrderByCreatedAtDesc(@Param("storeId") UUID storeId);

    // 메뉴명과 가게ID로 중복 체크
    @Query("SELECT COUNT(m) > 0 FROM Menu m WHERE m.name = :name AND m.store.id = :storeId")
    boolean existsByNameAndStoreId(@Param("name") String name, @Param("storeId") UUID storeId);

    // 인기 메뉴 조회 - 주문 횟수 기준
    @Query("SELECT m FROM Menu m ORDER BY m.createdAt DESC")
    List<Menu> findTopMenusByOrderCount(Pageable pageable);

    // 시간대별 인기 메뉴 조회
    @Query("SELECT m FROM Menu m WHERE m.createdAt BETWEEN :startTime AND :endTime ORDER BY m.createdAt DESC")
    List<Menu> findTopMenusByTimeRange(@Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime,
                                     Pageable pageable);

    // 지역별 인기 메뉴 조회
    @Query("SELECT m FROM Menu m ORDER BY m.createdAt DESC")
    List<Menu> findTopMenusByRegion(@Param("region") String region, Pageable pageable);
}
