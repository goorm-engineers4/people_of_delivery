package com.example.cloudfour.peopleofdelivery.domain.menu.controller;

import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.service.command.MenuCommandServiceImpl;
import com.example.cloudfour.peopleofdelivery.domain.menu.service.query.MenuQueryServiceImpl;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menus")
@Tag(name = "Menu", description = "메뉴 API by 정병민")
public class MenuController {
    private final MenuCommandServiceImpl menuCommandService;
    private final MenuQueryServiceImpl menuQueryService;
    @PostMapping("/{storeId}")
    @Operation(summary = "메뉴 생성", description = "메뉴를 생성합니다. 메뉴 생성에 사용되는 API입니다.")
    public CustomResponse<MenuResponseDTO.MenuDetailResponseDTO> createMenu(
            @PathVariable("storeId") UUID storeId,
            @RequestBody MenuRequestDTO.MenuCreateRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        MenuResponseDTO.MenuDetailResponseDTO result = menuCommandService.createMenu(requestDTO, storeId,userDetails);
        return CustomResponse.onSuccess(HttpStatus.CREATED, result);
    }

    @GetMapping("/{menuId}/detail")
    @Operation(summary = "메뉴 상세 조회", description = "메뉴의 상세 정보를 조회합니다. 메뉴 상세 조회에 사용되는 API입니다.")
    public CustomResponse<MenuResponseDTO.MenuDetailResponseDTO> getMenuDetail(
            @PathVariable("menuId") UUID menuId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        MenuResponseDTO.MenuDetailResponseDTO result = menuQueryService.getMenuDetail(menuId,userDetails);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @GetMapping("/{storeId}")
    @Operation(summary = "해당 가게 메뉴 목록 조회", description = "가게의 메뉴 목록을 조회합니다. 해당 가게의 메뉴를 조회하는 API입니다.")
    @Parameter(name = "cursor", description = "데이터가 시작하는 부분을 표시합니다")
    @Parameter(name = "size", description = "size만큼 데이터를 가져옵니다.")
    public CustomResponse<MenuResponseDTO.MenuStoreListResponseDTO> getMenusByStore(
            @PathVariable("storeId") UUID storeId,
            @RequestParam(name = "cursor", required = false) LocalDateTime cursor,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        MenuResponseDTO.MenuStoreListResponseDTO result = menuQueryService.getMenusByStoreWithCursor(storeId, cursor, size,userDetails);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @GetMapping("/{storeId}/category")
    @Operation(summary = "해당 가게 메뉴 카테고리 별 목록 조회",
            description = "가게의 카테고리 별 목록을 조회합니다. 해당 가게의 카테고리 별 목록을 조회하는 API입니다.")
    @Parameter(name = "cursor", description = "데이터가 시작하는 부분을 표시합니다")
    @Parameter(name = "size", description = "size만큼 데이터를 가져옵니다.")
    public CustomResponse<MenuResponseDTO.MenuStoreListResponseDTO> getMenusByCategory(
            @PathVariable("storeId") UUID storeId,
            @RequestParam(name = "categoryId")  UUID categoryId,
            @RequestParam(name = "cursor", required = false) LocalDateTime cursor,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        MenuResponseDTO.MenuStoreListResponseDTO result = menuQueryService.getMenusByStoreWithCategory(storeId, categoryId, cursor, size,userDetails);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @GetMapping("/top")
    @Operation(summary = "인기 메뉴 TOP20 조회", description = "인기 메뉴 TOP20을 조회합니다. 인기 메뉴 조회에 사용되는 API입니다.")
    public CustomResponse<List<MenuResponseDTO.MenuTopResponseDTO>> getTopMenus(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<MenuResponseDTO.MenuTopResponseDTO> result = menuQueryService.getTopMenus(userDetails);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @GetMapping("/timetop")
    @Operation(summary = "시간대별 인기 메뉴 TOP20 조회", description = "시간대별 인기 메뉴 TOP20을 조회합니다. 시간대별 인기 메뉴 조회에 사용되는 API입니다.")
    public CustomResponse<List<MenuResponseDTO.MenuTimeTopResponseDTO>> getTimeTopMenus(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<MenuResponseDTO.MenuTimeTopResponseDTO> result = menuQueryService.getTimeTopMenus(userDetails);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @GetMapping("/regiontop")
    @Operation(summary = "지역별 인기 메뉴 TOP20 조회", description = "지역별 인기 메뉴 TOP20을 조회합니다. 지역별 인기 메뉴 조회에 사용되는 API입니다.")
    public CustomResponse<List<MenuResponseDTO.MenuRegionTopResponseDTO>> getRegionTopMenus(
            @RequestParam String si,
            @RequestParam String gu,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<MenuResponseDTO.MenuRegionTopResponseDTO> result = menuQueryService.getRegionTopMenus(si, gu,userDetails);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @PatchMapping("/{menuId}")
    @Operation(summary = "메뉴 수정", description = "메뉴를 수정합니다. 메뉴 수정에 사용되는 API입니다.")
    public CustomResponse<MenuResponseDTO.MenuDetailResponseDTO> updateMenu(
            @RequestBody MenuRequestDTO.MenuUpdateRequestDTO requestDTO,
            @PathVariable("menuId") UUID menuId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        MenuResponseDTO.MenuDetailResponseDTO result = menuCommandService.updateMenu(menuId, requestDTO, userDetails);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @DeleteMapping("/{menuId}/deleted")
    @Operation(summary = "메뉴 삭제", description = "메뉴를 삭제합니다. 메뉴 삭제에 사용되는 API입니다.")
    public CustomResponse<String> deleteMenu(
            @PathVariable("menuId") UUID menuId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        menuCommandService.deleteMenu(menuId, userDetails);
        return CustomResponse.onSuccess(HttpStatus.OK, "메뉴 삭제 완료");
    }
}
