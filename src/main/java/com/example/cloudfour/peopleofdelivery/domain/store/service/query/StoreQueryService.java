package com.example.cloudfour.peopleofdelivery.domain.store.service.query;

import com.example.cloudfour.peopleofdelivery.domain.store.dto.StoreResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StoreQueryService {

    // 모든 가게 조회
    public List<StoreResponseDTO.StoreListResponseDTO> getAllStores() {


        return null;
    }


    //(특정 한개의) 가게 상세 정보 조회
    public StoreResponseDTO.StoreDetailResponseDTO getStoreById(UUID storeId) {



        return null;
    }
}
