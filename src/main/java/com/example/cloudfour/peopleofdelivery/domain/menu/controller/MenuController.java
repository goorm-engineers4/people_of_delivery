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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
    public CustomResponse<List<MenuResponseDTO.MenuRegionTopResponseDTO>> getRegionTopMenus(
            @RequestParam String si,
            @RequestParam String gu) {
        List<MenuResponseDTO.MenuRegionTopResponseDTO> result = menuQueryService.getRegionTopMenus(si, gu);
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
        return CustomResponse.onSuccess(HttpStatus.OK, "메뉴 삭제 완료");
    }
}
