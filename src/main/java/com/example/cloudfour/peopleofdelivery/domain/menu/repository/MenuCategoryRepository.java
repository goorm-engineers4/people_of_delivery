package com.example.cloudfour.peopleofdelivery.domain.menu.repository;

import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, UUID> {

    // 카테고리명으로 조회, 카테고리명 중복 체크, 활성 상태 카테고리만 조회
}
