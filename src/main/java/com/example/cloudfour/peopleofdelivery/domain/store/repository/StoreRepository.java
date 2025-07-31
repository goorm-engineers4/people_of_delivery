package com.example.cloudfour.peopleofdelivery.domain.store.repository;

import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {
    // 가게 이름 중복 여부 확인
    boolean existsByName(String name);

    // 특정 카테고리 ID에 해당하는 가게 목록 조회
    List<Store> findAllByStoreCategoryId(UUID categoryId);
}
