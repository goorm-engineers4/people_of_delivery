package com.example.cloudfour.peopleofdelivery.domain.menu.repository;

import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MenuOptionRepository extends JpaRepository<MenuOption, UUID> {
    // 특정 메뉴의 옵션들 조회
    @Query("select mo from MenuOption mo join fetch OrderItem oi on oi.menu.id = mo.menu.id where oi.order.id =:OrderId")
    List<MenuOption> findByOrderId(@Param("OrderId") UUID orderId);
}