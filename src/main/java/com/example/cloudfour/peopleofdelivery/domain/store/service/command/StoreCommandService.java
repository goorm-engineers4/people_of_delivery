package com.example.cloudfour.peopleofdelivery.domain.store.service.command;

import com.example.cloudfour.peopleofdelivery.domain.region.entity.Region;
import com.example.cloudfour.peopleofdelivery.domain.region.repository.RegionRepository;
import com.example.cloudfour.peopleofdelivery.domain.store.converter.StoreCategoryConverter;
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
import com.example.cloudfour.peopleofdelivery.domain.user.exception.UserErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.user.exception.UserException;
import com.example.cloudfour.peopleofdelivery.domain.user.repository.UserRepository;
import com.example.cloudfour.peopleofdelivery.global.auth.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class StoreCommandService {

    private final StoreRepository storeRepository;
    private final RegionRepository regionRepository;
    private final StoreCategoryRepository storeCategoryRepository;
    private final UserRepository userRepository;

    public StoreResponseDTO.StoreCreateResponseDTO createStore(StoreRequestDTO.StoreCreateRequestDTO dto, CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId()).orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));
        if (!(user.getRole() == Role.MASTER || user.getRole() == Role.OWNER)) {
            throw new StoreException(StoreErrorCode.UNAUTHORIZED_ACCESS);
        }
        if (storeRepository.existsByName(dto.getName())) {
            throw new StoreException(StoreErrorCode.ALREADY_ADD);
        }

        StoreCategory category = storeCategoryRepository
                .findByCategory(dto.getCategory())
                .orElseGet(() -> storeCategoryRepository.save(
                        StoreCategoryConverter.toStoreCategory(dto.getCategory())
                ));
        Region region = regionRepository.findById(dto.getRegionId()).orElseThrow(() -> new StoreException(StoreErrorCode.NOT_FOUND));

        Store store = StoreConverter.toStore(dto);
        store.setStoreCategory(category);
        store.setRegion(region);
        //TODO store의 address에 따라 region 생성하고 저장
        store.setUser(user);
        storeRepository.save(store);
        return StoreConverter.toStoreCreateResponseDTO(store);
    }

    public StoreResponseDTO.StoreUpdateResponseDTO updateStore(UUID storeId, StoreRequestDTO.StoreUpdateRequestDTO dto, CustomUserDetails userDetails) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.NOT_FOUND));
        User user = userRepository.findById(userDetails.getId()).orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));
        if (storeRepository.existsByStoreAndUser(storeId, user.getId())) {
            throw new StoreException(StoreErrorCode.UNAUTHORIZED_ACCESS);
        }
        if (storeRepository.existsByName(dto.getName())) {
            throw new StoreException(StoreErrorCode.ALREADY_ADD);
        }
        StoreCategory category = storeCategoryRepository
                .findByCategory(dto.getCategory())
                .orElseGet(() -> storeCategoryRepository.save(
                        StoreCategoryConverter.toStoreCategory(dto.getCategory())
                ));
        if (!store.getUser().getId().equals(user.getId())) {
            throw new StoreException(StoreErrorCode.UNAUTHORIZED_ACCESS);
        }

        store.update(dto.getName(), dto.getAddress());
        store.setStoreCategory(category);
        //TODO store의 address에 따라 region 생성하고 저장
        storeRepository.save(store);

        return StoreResponseDTO.StoreUpdateResponseDTO.from(store);
    }

    public void deleteStore(UUID storeId, CustomUserDetails userDetails) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.NOT_FOUND));
        User user = userRepository.findById(userDetails.getId()).orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));
        if (!storeRepository.existsByStoreAndUser(storeId, user.getId())) {
            throw new StoreException(StoreErrorCode.UNAUTHORIZED_ACCESS);
        }
        store.softDelete();
        storeRepository.save(store);
    }
}
