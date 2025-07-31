package com.example.cloudfour.peopleofdelivery.domain.store.service.query;

import com.example.cloudfour.peopleofdelivery.domain.store.dto.StoreResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreQueryService {

    private final StoreRepository storeRepository;

    // 전체 가게 목록 조회
    public List<StoreResponseDTO.StoreListResponseDTO> getAllStores() {
        List<Store> stores = storeRepository.findAll();
        return stores.stream()
                .map(StoreResponseDTO.StoreListResponseDTO::from)
                .collect(Collectors.toList());
    }

    // 카테고리별 가게 목록 조회
    public List<StoreResponseDTO.StoreListResponseDTO> getStoresByCategory(UUID categoryId) {
        List<Store> stores = storeRepository.findAllByStoreCategoryId(categoryId);
        return stores.stream()
                .map(StoreResponseDTO.StoreListResponseDTO::from)
                .collect(Collectors.toList());
    }

    // 특정 가게 상세 정보 조회
    public StoreResponseDTO.StoreDetailResponseDTO getStoreById(UUID storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가게를 찾을 수 없습니다."));
        return StoreResponseDTO.StoreDetailResponseDTO.from(store);
    }
}
