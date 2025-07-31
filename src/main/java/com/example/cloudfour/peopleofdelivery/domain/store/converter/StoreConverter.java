package com.example.cloudfour.peopleofdelivery.domain.store.converter;

import com.example.cloudfour.peopleofdelivery.domain.region.entity.Region;
import com.example.cloudfour.peopleofdelivery.domain.store.dto.StoreRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.store.dto.StoreResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.StoreCategory;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;

public class StoreConverter {

    // CreateRequestDTO → Entity 변환
    public static Store toStore(StoreRequestDTO.StoreCreateRequestDTO dto) {
        return Store.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .storePicture(dto.getStorePicture())
                .phone(dto.getPhone())
                .content(dto.getContent())
                .minPrice(dto.getMinPrice())
                .deliveryTip(dto.getDeliveryTip())
                .operationHours(dto.getOperationHours())
                .closedDays(dto.getClosedDays())
                .rating(0f)
                .likeCount(0)
                .reviewCount(0)
                .build();
    }

    // Entity → CreateResponseDTO 변환
    public static StoreResponseDTO.StoreCreateResponseDTO toStoreCreateResponseDTO(Store store) {
        return StoreResponseDTO.StoreCreateResponseDTO.builder()
                .storeId(store.getId())
                .name(store.getName())
                .address(store.getAddress())
                .storePicture(store.getStorePicture())
                .phone(store.getPhone())
                .content(store.getContent())
                .minPrice(store.getMinPrice())
                .deliveryTip(store.getDeliveryTip())
                .operationHours(store.getOperationHours())
                .closedDays(store.getClosedDays())
                .category(store.getStoreCategory().getCategory())
                .createdAt(store.getCreatedAt())
                .createdBy(store.getUser().getId())
                .build();
    }
}
