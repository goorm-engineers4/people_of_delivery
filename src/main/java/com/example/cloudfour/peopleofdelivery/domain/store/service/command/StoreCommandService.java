package com.example.cloudfour.peopleofdelivery.domain.store.service.command;

import com.example.cloudfour.peopleofdelivery.domain.region.repository.RegionRepository;
import com.example.cloudfour.peopleofdelivery.domain.store.converter.StoreConverter;
import com.example.cloudfour.peopleofdelivery.domain.store.dto.StoreRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.store.dto.StoreResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.StoreCategory;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreCategoryRepository;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.domain.region.entity.Region;

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
        // 0. 권한 체크
        if (!(user.getRole() == Role.MASTER || user.getRole() == Role.OWNER)) {
            throw new IllegalArgumentException("가게를 생성할 권한이 없습니다.");
        }

        // 1. 가게 이름 중복 체크
        if (storeRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("이미 존재하는 가게 이름입니다.");
        }


        // 2. 카테고리 존재 여부 확인
        StoreCategory category = storeCategoryRepository.findById(dto.getStoreCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다."));

        // 3. 지역 조회 (nullable) -> 일단 이거는 테스트 코드 부분 어케 로직을 짜야 할지
        Region region = null; //임시적으로 null 로 하고 작업
//        if (dto.getRegionId() != null) {
//            region = regionRepository.findById(dto.getRegionId())
//                    .orElseThrow(() -> new IllegalArgumentException("해당 지역을 찾을 수 없습니다."));
//        }

        // 3. Store 엔티티로 변환
        Store store = StoreConverter.toStore(dto, category, user, region);

        // 4. 엔티티로 변환한 데이터 DB에 저장
        // 아직 레포구현이 안되어있으니까 에러가 나는게 맞는데,
        // 테스트코드실행할 때는 까자 레포 객체를 쓰니까 실행 되어야 하는건가
        storeRepository.save(store);

        // 5. 엔티티를 다시 응답 DTO로 변환 후 반환
        return StoreConverter.toStoreCreateResponseDTO(store);
    }


    // 가게 정보 수정
    public StoreResponseDTO.StoreUpdateResponseDTO updateStore(UUID storeId, StoreRequestDTO.StoreUpdateRequestDTO dto, User user) {
        return null;
    }



    //가게 삭제
    public void deleteStore(UUID storeId, User user) {
        // 아무 동작 안 함
    }





}
