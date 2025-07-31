package com.example.cloudfour.peopleofdelivery.domain.menu.controller;

import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.service.command.MenuCommandServiceImpl;
import com.example.cloudfour.peopleofdelivery.domain.menu.service.query.MenuQueryServiceImpl;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/*
 < HTTP 상태 코드 >
  - 201 CREATED: 성공적으로 생성됨
  - 200 OK: 성공적으로 처리됨
  - 400 BAD REQUEST: 잘못된 요청
  - 500 INTERNAL SERVER ERROR: 서버 내부 오류

 < GET, POST, PUT, PATCH, DELETE 메소드 >
  - GET: 데이터 조회
  - POST: 새로운 데이터를 생성
  - PUT: 기존 데이터를 전체적으로 수정
  - PATCH: 기존 데이터를 삭제하지 않고 일부 수정
  - DELETE: 기존 데이터를 삭제

 < Swagger 주석 >
  - @Operation: API의 동작을 설명
  - @Tag: API 그룹화 및 설명
  - @RequestBody: 요청 본문을 DTO로 매핑
  - @PathVariable: URL 경로에 변수 매핑
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menus")
@Tag(name = "Menu", description = "메뉴 API by 정병민")
public class MenuController {
    private final MenuCommandServiceImpl menuCommandService; // 메뉴 생성/수정/삭제
    private final MenuQueryServiceImpl menuQueryService; // 메뉴 조회
    @PostMapping("")
    @Operation(summary = "메뉴 생성", description = "메뉴를 생성합니다. 메뉴 생성에 사용되는 API입니다.")
    public CustomResponse<MenuResponseDTO.MenuDetailResponseDTO> createMenu(
            @RequestBody MenuRequestDTO.MenuCreateRequestDTO requestDTO,
            @AuthenticationPrincipal User user) {
        MenuResponseDTO.MenuDetailResponseDTO result = menuCommandService.createMenu(requestDTO, user);
        return CustomResponse.onSuccess(HttpStatus.CREATED, result);
    }

    @GetMapping("/{menuId}/detail")
    @Operation(summary = "메뉴 상세 조회", description = "메뉴의 상세 정보를 조회합니다. 메뉴 상세 조회에 사용되는 API입니다.")
    public CustomResponse<MenuResponseDTO.MenuDetailResponseDTO> getMenuDetail(
            @PathVariable("menuId") UUID menuId) {
        MenuResponseDTO.MenuDetailResponseDTO result = menuQueryService.getMenuDetail(menuId);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @GetMapping("/{storeId}")
    @Operation(summary = "해당 가게 메뉴 목록 조회", description = "가게의 메뉴 목록을 조회합니다. 해당 가게의 메뉴를 조회하는 API입니다.")
    @Parameter(name = "cursor", description = "데이터가 시작하는 부분을 표시합니다")
    @Parameter(name = "size", description = "size만큼 데이터를 가져옵니다.")
    public CustomResponse<MenuResponseDTO.MenuStoreListResponseDTO> getMenusByStore(
            @PathVariable("storeId") UUID storeId,
            @RequestParam(name = "cursor") LocalDateTime cursor,
            @RequestParam(name = "size") Integer size) {
        MenuResponseDTO.MenuStoreListResponseDTO result = menuQueryService.getMenusByStoreWithCursor(storeId, cursor, size);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @GetMapping("/top")
    @Operation(summary = "인기 메뉴 TOP20 조회", description = "인기 메뉴 TOP20을 조회합니다. 인기 메뉴 조회에 사용되는 API입니다.")
    public CustomResponse<List<MenuResponseDTO.MenuTopResponseDTO>> getTopMenus() {
        List<MenuResponseDTO.MenuTopResponseDTO> result = menuQueryService.getTopMenus();
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @GetMapping("/timetop")
    @Operation(summary = "시간대별 인기 메뉴 TOP20 조회", description = "시간대별 인기 메뉴 TOP20을 조회합니다. 시간대별 인기 메뉴 조회에 사용되는 API입니다.")
    public CustomResponse<List<MenuResponseDTO.MenuTimeTopResponseDTO>> getTimeTopMenus() {
        List<MenuResponseDTO.MenuTimeTopResponseDTO> result = menuQueryService.getTimeTopMenus();
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @GetMapping("/regiontop")
    @Operation(summary = "지역별 인기 메뉴 TOP20 조회", description = "지역별 인기 메뉴 TOP20을 조회합니다. 지역별 인기 메뉴 조회에 사용되는 API입니다.")
    public CustomResponse<List<MenuResponseDTO.MenuRegionTopResponseDTO>> getRegionTopMenus() {
        List<MenuResponseDTO.MenuRegionTopResponseDTO> result = menuQueryService.getRegionTopMenus();
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @PatchMapping("/{menuId}")
    @Operation(summary = "메뉴 수정", description = "메뉴를 수정합니다. 메뉴 수정에 사용되는 API입니다.")
    public CustomResponse<MenuResponseDTO.MenuDetailResponseDTO> updateMenu(
            @RequestBody MenuRequestDTO.MenuUpdateRequestDTO requestDTO,
            @PathVariable("menuId") UUID menuId,
            @AuthenticationPrincipal User user) {
        MenuResponseDTO.MenuDetailResponseDTO result = menuCommandService.updateMenu(menuId, requestDTO, user);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @PatchMapping("/{menuId}/delete")
    @Operation(summary = "메뉴 삭제", description = "메뉴를 삭제합니다. 메뉴 삭제에 사용되는 API입니다.")
    public CustomResponse<String> deleteMenu(
            @PathVariable("menuId") UUID menuId,
            @AuthenticationPrincipal User user) {
        menuCommandService.deleteMenu(menuId, user);
        return CustomResponse.onSuccess(HttpStatus.OK, "메뉴 삭제 완료.");
    }
}
