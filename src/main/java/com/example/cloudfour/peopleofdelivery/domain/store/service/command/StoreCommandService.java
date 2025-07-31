package com.example.cloudfour.peopleofdelivery.domain.store.service.command;

import com.example.cloudfour.peopleofdelivery.domain.region.entity.Region;
import com.example.cloudfour.peopleofdelivery.domain.region.repository.RegionRepository;
import com.example.cloudfour.peopleofdelivery.domain.store.converter.StoreConverter;
import com.example.cloudfour.peopleofdelivery.domain.store.dto.StoreRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.store.dto.StoreResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.StoreCategory;
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


    // 가게 생성
    public StoreResponseDTO.StoreCreateResponseDTO createStore(StoreRequestDTO.StoreCreateRequestDTO dto, User user) {
        if (!(user.getRole() == Role.MASTER || user.getRole() == Role.OWNER)) {
            throw new IllegalArgumentException("가게를 생성할 권한이 없습니다.");
        }

        if (storeRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("이미 존재하는 가게 이름입니다.");
        }

        StoreCategory category = storeCategoryRepository.findById(dto.getStoreCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다."));

        Region region = regionRepository.findById(dto.getRegionId())
                .orElseThrow(() -> new IllegalArgumentException("해당 지역을 찾을 수 없습니다."));

        Store store = StoreConverter.toStore(dto, category, user, region);
        storeRepository.save(store);

        return StoreConverter.toStoreCreateResponseDTO(store);
    }

    // 가게 정보 수정
    public StoreResponseDTO.StoreUpdateResponseDTO updateStore(UUID storeId, StoreRequestDTO.StoreUpdateRequestDTO dto, User user) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가게를 찾을 수 없습니다."));

        if (!store.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인의 가게만 수정할 수 있습니다.");
        }

        if (dto.getName() != null) store.setName(dto.getName());
        if (dto.getAddress() != null) store.setAddress(dto.getAddress());
        if (dto.getRating() >= 0) store.setRating(dto.getRating());
        if (dto.getReviewCount() >= 0) store.setReviewCount(dto.getReviewCount());

        storeRepository.save(store);

        return StoreResponseDTO.StoreUpdateResponseDTO.builder().build(); // 응답 내용 필요 시 추가
    }

    // 가게 삭제
    public void deleteStore(UUID storeId, User user) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가게를 찾을 수 없습니다."));

        if (!store.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인의 가게만 삭제할 수 있습니다.");
        }

        storeRepository.delete(store);
    }
}
