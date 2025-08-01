package com.example.cloudfour.peopleofdelivery.domain.store.service.command;

import com.example.cloudfour.peopleofdelivery.domain.region.entity.Region;
import com.example.cloudfour.peopleofdelivery.domain.region.repository.RegionRepository;
import com.example.cloudfour.peopleofdelivery.domain.store.converter.StoreConverter;
import com.example.cloudfour.peopleofdelivery.domain.store.dto.StoreRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.store.dto.StoreResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.StoreCategory;
import com.example.cloudfour.peopleofdelivery.domain.store.exception.StoreErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.store.exception.StoreException;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreCategoryRepository;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class StoreCommandService {

    private final StoreRepository storeRepository;
    private final RegionRepository regionRepository;
    private final StoreCategoryRepository storeCategoryRepository;

    public StoreResponseDTO.StoreCreateResponseDTO createStore(StoreRequestDTO.StoreCreateRequestDTO dto, User user) {
        if (!(user.getRole() == Role.MASTER || user.getRole() == Role.OWNER)) {
            throw new StoreException(StoreErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (storeRepository.existsByName(dto.getName())) {
            throw new StoreException(StoreErrorCode.ALREADY_ADD);
        }

        StoreCategory category = storeCategoryRepository.findById(dto.getStoreCategoryId())
                .orElseThrow(() -> new StoreException(StoreErrorCode.NOT_FOUND));

        Region region = regionRepository.findById(dto.getRegionId())
                .orElseThrow(() -> new StoreException(StoreErrorCode.NOT_FOUND));

        Store store = StoreConverter.toStore(dto);
        store.setStoreCategory(category);
        store.setRegion(region);
        store.setUser(user);
        storeRepository.save(store);

        return StoreConverter.toStoreCreateResponseDTO(store);
    }

    public StoreResponseDTO.StoreUpdateResponseDTO updateStore(UUID storeId, StoreRequestDTO.StoreUpdateRequestDTO dto, User user) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.NOT_FOUND));

        if (!store.getUser().getId().equals(user.getId())) {
            throw new StoreException(StoreErrorCode.UNAUTHORIZED_ACCESS);
        }

        store.update(dto.getName(), dto.getAddress(), dto.getRating(), dto.getReviewCount());

        storeRepository.save(store);

        return StoreResponseDTO.StoreUpdateResponseDTO.builder().build();
    }

    public void deleteStore(UUID storeId, User user) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.NOT_FOUND));

        if (!store.getUser().getId().equals(user.getId())) {
            throw new StoreException(StoreErrorCode.UNAUTHORIZED_ACCESS);
        }

        store.softDelete();
    }
}
