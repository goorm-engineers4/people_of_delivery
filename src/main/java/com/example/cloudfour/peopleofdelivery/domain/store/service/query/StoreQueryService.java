package com.example.cloudfour.peopleofdelivery.domain.store.service.query;

import com.example.cloudfour.peopleofdelivery.domain.store.dto.StoreResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.store.exception.StoreErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.store.exception.StoreException;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.exception.UserErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.user.exception.UserException;
import com.example.cloudfour.peopleofdelivery.domain.user.repository.UserRepository;
import com.example.cloudfour.peopleofdelivery.global.auth.userdetails.CustomUserDetails;
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
    private final UserRepository userRepository;

    public StoreResponseDTO.StoreCursorListResponseDTO getAllStores(LocalDateTime cursor, int size, String keyword, CustomUserDetails userDetails) {
        userRepository.findById(userDetails.getId()).orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));

        LocalDateTime baseTime = (cursor != null) ? cursor : LocalDateTime.now();

        Pageable pageable = PageRequest.of(0, size);
        Slice<Store> storeSlice = storeRepository.findAllByKeyWord(keyword,baseTime, pageable);

        List<StoreResponseDTO.StoreListResponseDTO> storeList = storeSlice.getContent().stream()
                .map(StoreResponseDTO.StoreListResponseDTO::from)
                .toList();

        LocalDateTime nextCursor = storeSlice.hasNext() && !storeList.isEmpty()
                ? storeList.get(storeList.size() - 1).getCreatedAt()
                : null;

        return StoreResponseDTO.StoreCursorListResponseDTO.of(storeList, nextCursor);
    }

    public StoreResponseDTO.StoreCursorListResponseDTO getStoresByCategory(UUID categoryId, LocalDateTime cursor, int size,CustomUserDetails userDetails) {
        userRepository.findById(userDetails.getId()).orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));

        LocalDateTime baseTime = (cursor != null) ? cursor : LocalDateTime.now();

        Pageable pageable = PageRequest.of(0, size);
        Slice<Store> storeSlice = storeRepository.findAllByCategoryAndCursor(categoryId, baseTime, pageable);

        List<StoreResponseDTO.StoreListResponseDTO> storeList = storeSlice.getContent().stream()
                .map(StoreResponseDTO.StoreListResponseDTO::from)
                .toList();

        LocalDateTime nextCursor = storeSlice.hasNext() && !storeList.isEmpty()
                ? storeList.get(storeList.size() - 1).getCreatedAt()
                : null;

        return StoreResponseDTO.StoreCursorListResponseDTO.of(storeList, nextCursor);
    }

    public StoreResponseDTO.StoreDetailResponseDTO getStoreById(UUID storeId,CustomUserDetails userDetails) {
        userRepository.findById(userDetails.getId()).orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.NOT_FOUND));
        return StoreResponseDTO.StoreDetailResponseDTO.from(store);
    }
}

