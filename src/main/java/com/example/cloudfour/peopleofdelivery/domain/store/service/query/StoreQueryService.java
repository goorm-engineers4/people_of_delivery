package com.example.cloudfour.peopleofdelivery.domain.store.service.query;

import com.example.cloudfour.peopleofdelivery.domain.store.dto.StoreResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreQueryService {

    private final StoreRepository storeRepository;

    public StoreResponseDTO.StoreCursorListResponseDTO getAllStores(LocalDateTime cursor, int size) {
        LocalDateTime baseTime = (cursor != null) ? cursor : LocalDateTime.now();

        Pageable pageable = PageRequest.of(0, size);
        Slice<Store> storeSlice = storeRepository.findAllByCursor(baseTime, pageable);

        List<StoreResponseDTO.StoreListResponseDTO> storeList = storeSlice.getContent().stream()
                .map(StoreResponseDTO.StoreListResponseDTO::from)
                .toList();
        // 빈 리스트에서 꺼내는거 방지하기 위해 조건 추가
        LocalDateTime nextCursor = storeSlice.hasNext() && !storeList.isEmpty()
                ? storeList.get(storeList.size() - 1).getCreatedAt()
                : null;

        return StoreResponseDTO.StoreCursorListResponseDTO.of(storeList, nextCursor);
    }

    public StoreResponseDTO.StoreCursorListResponseDTO getStoresByCategory(UUID categoryId, LocalDateTime cursor, int size) {
        LocalDateTime baseTime = (cursor != null) ? cursor : LocalDateTime.now();

        Pageable pageable = PageRequest.of(0, size);
        Slice<Store> storeSlice = storeRepository.findAllByCategoryAndCursor(categoryId, baseTime, pageable);

        List<StoreResponseDTO.StoreListResponseDTO> storeList = storeSlice.getContent().stream()
                .map(StoreResponseDTO.StoreListResponseDTO::from)
                .toList();

        // 빈 리스트에서 꺼내는거 방지하기 위해 조건 추가
        LocalDateTime nextCursor = storeSlice.hasNext() && !storeList.isEmpty()
                ? storeList.get(storeList.size() - 1).getCreatedAt()
                : null;

        return StoreResponseDTO.StoreCursorListResponseDTO.of(storeList, nextCursor);
    }

    public StoreResponseDTO.StoreDetailResponseDTO getStoreById(UUID storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가게를 찾을 수 없습니다."));
        return StoreResponseDTO.StoreDetailResponseDTO.from(store);
    }
}

