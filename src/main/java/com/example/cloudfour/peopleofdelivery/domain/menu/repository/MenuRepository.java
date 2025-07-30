package com.example.cloudfour.peopleofdelivery.domain.menu.repository;

import com.example.cloudfour.peopleofdelivery.domain.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface MenuRepository extends JpaRepository<Menu, UUID> {

    // 가게별 활성 메뉴 조회, 가격 범위로 메뉴 검색, 카테고리별 메뉴 조회
}
