package com.example.cloudfour.peopleofdelivery.domain.store.controller;

import com.example.cloudfour.peopleofdelivery.domain.store.dto.StoreRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.store.dto.StoreResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.store.service.command.StoreCommandService;
import com.example.cloudfour.peopleofdelivery.domain.store.service.query.StoreQueryService;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.CustomResponse;
import com.example.cloudfour.peopleofdelivery.global.auth.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
@Tag(name = "Store", description = "가게 API by 지윤")
public class StoreController {

    private final StoreCommandService storeCommandService;
    private final StoreQueryService storeQueryService;

    @PostMapping("")
    @Operation(summary = "가게 등록", description = "가게를 등록합니다.")
    public CustomResponse<StoreResponseDTO.StoreCreateResponseDTO> createStore(
            @RequestBody StoreRequestDTO.StoreCreateRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return CustomResponse.onSuccess(HttpStatus.CREATED, storeCommandService.createStore(dto, userDetails));
    }

    @GetMapping("")
    @Operation(summary = "가게 목록 조회", description = "전체 가게 목록을 커서 기반으로 조회합니다.")
    @Parameter(name = "cursor", description = "데이터가 시작하는 기준 시간입니다.")
    @Parameter(name = "size", description = "가져올 데이터 수입니다.")
    public CustomResponse<StoreResponseDTO.StoreCursorListResponseDTO> getStoreList(
            @RequestParam(name = "cursor", required = false) LocalDateTime cursor,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "keyword", defaultValue = "햄버거")  String keyword,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        StoreResponseDTO.StoreCursorListResponseDTO response = storeQueryService.getAllStores(cursor, size,keyword,userDetails);
        return CustomResponse.onSuccess(HttpStatus.OK, response);
    }

    @GetMapping("/{storeId}")
    @Operation(summary = "가게 상세 정보 조회", description = "가게의 상세 정보를 조회합니다.")
    public CustomResponse<StoreResponseDTO.StoreDetailResponseDTO> getStoreDetail(
            @PathVariable UUID storeId,@AuthenticationPrincipal CustomUserDetails userDetails) {
        return CustomResponse.onSuccess(HttpStatus.OK, storeQueryService.getStoreById(storeId,userDetails));
    }

    @PatchMapping("/{storeId}")
    @Operation(summary = "가게 정보 수정", description = "본인의 가게 정보를 수정합니다.")
    public CustomResponse<StoreResponseDTO.StoreUpdateResponseDTO> updateStore(
            @PathVariable UUID storeId,
            @RequestBody StoreRequestDTO.StoreUpdateRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return CustomResponse.onSuccess(HttpStatus.OK, storeCommandService.updateStore(storeId, dto, userDetails));
    }

    @PatchMapping("/{storeId}/deleted")
    @Operation(summary = "가게 삭제", description = "본인의 가게를 삭제합니다.")
    public CustomResponse<String> deleteStore(
            @PathVariable UUID storeId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        storeCommandService.deleteStore(storeId, userDetails);
        return CustomResponse.onSuccess(HttpStatus.OK, "가게 삭제 완료");
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "카테고리별 가게 목록 조회", description = "카테고리 ID로 해당 카테고리의 가게 목록을 커서 기반으로 조회합니다.")
    @Parameter(name = "cursor", description = "데이터가 시작하는 기준 시간입니다.")
    @Parameter(name = "size", description = "가져올 데이터 수입니다.")
    public CustomResponse<StoreResponseDTO.StoreCursorListResponseDTO> getStoresByCategory(
            @PathVariable UUID categoryId,
            @RequestParam(name = "cursor", required = false) LocalDateTime cursor,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        StoreResponseDTO.StoreCursorListResponseDTO response = storeQueryService.getStoresByCategory(categoryId, cursor, size,userDetails);
        return CustomResponse.onSuccess(HttpStatus.OK, response);
    }
}
