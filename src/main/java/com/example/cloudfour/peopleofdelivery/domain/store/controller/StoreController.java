
package com.example.cloudfour.peopleofdelivery.domain.store.controller;

import com.example.cloudfour.peopleofdelivery.domain.store.dto.StoreRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.store.dto.StoreResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.store.service.command.StoreCommandService;
import com.example.cloudfour.peopleofdelivery.domain.store.service.query.StoreQueryService;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
@Tag(name = "Store", description = "가게 API by 지윤")
public class StoreController { //가게 컨트롤러 클래스 정의

    // 가게 등록, 수정, 삭제 (쓰기 관련) 처리하는 서비스
    private final StoreCommandService storeCommandService;
    // 가게 조회 관련 서비스
    private final StoreQueryService storeQueryService;

    // POST /api/stores
    @PostMapping("")
    @Operation(summary = "가게 등록", description = "가게를 등록합니다.")
    public CustomResponse<StoreResponseDTO.StoreCreateResponseDTO> createStore(
            @RequestBody StoreRequestDTO.StoreCreateRequestDTO dto,
            @AuthenticationPrincipal User user) {
        return CustomResponse.onSuccess(HttpStatus.CREATED, storeCommandService.createStore(dto, user));
    }

    // GET /api/stores
    @GetMapping("")
    @Operation(summary = "가게 목록 조회", description = "전체 가게 목록을 조회합니다.")
    public CustomResponse<List<StoreResponseDTO.StoreListResponseDTO>> getStoreList() {
        return CustomResponse.onSuccess(HttpStatus.OK, storeQueryService.getAllStores());
    }

    //GET /api/stores/{storeId}
    @GetMapping("/{storeId}")
    @Operation(summary = "가게 상세 정보 조회", description = "가게의 상세 정보를 조회합니다.")
    public CustomResponse<StoreResponseDTO.StoreDetailResponseDTO> getStoreDetail(
            @PathVariable UUID storeId) {
        return CustomResponse.onSuccess(HttpStatus.OK, storeQueryService.getStoreById(storeId));
    }

    // PATCH /api/stores/{storeId}
    @PatchMapping("/{storeId}")
    @Operation(summary = "가게 정보 수정", description = "본인의 가게 정보를 수정합니다.")
    public CustomResponse<StoreResponseDTO.StoreUpdateResponseDTO> updateStore(
            @PathVariable UUID storeId,
            @RequestBody StoreRequestDTO.StoreUpdateRequestDTO dto,
            @AuthenticationPrincipal User user) {
        return CustomResponse.onSuccess(HttpStatus.OK, storeCommandService.updateStore(storeId, dto, user));
    }

    // PATCH /api/stores/{storeID}/deleted
    @PatchMapping("/{storeId}/deleted")
    @Operation(summary = "가게 삭제", description = "본인의 가게를 삭제합니다.")
    public CustomResponse<String> deleteStore(
            @PathVariable UUID storeId,
            @AuthenticationPrincipal User user) {
        storeCommandService.deleteStore(storeId, user);
        return CustomResponse.onSuccess(HttpStatus.OK, "가게 삭제 완료");
    }

}

