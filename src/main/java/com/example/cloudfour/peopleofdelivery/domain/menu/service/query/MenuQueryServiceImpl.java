package com.example.cloudfour.peopleofdelivery.domain.menu.service.query;

import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.Menu;
import com.example.cloudfour.peopleofdelivery.domain.menu.exception.MenuException;
import com.example.cloudfour.peopleofdelivery.domain.menu.exception.MenuErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service // 서비스 레이어 어노테이션
@Slf4j // 로그 찍기 기능
@RequiredArgsConstructor // 생성자 주입을 위한 어노테이션
@Transactional(readOnly = true) // 트랜잭션 관리 어노테이션
public class MenuQueryServiceImpl {

    private final MenuRepository menuRepository;
    private static final LocalDateTime first_cursor = LocalDateTime.now().plusDays(1);

    // 특정 가게의 메뉴 목록 조회
    public List<MenuResponseDTO.MenuListResponseDTO> getMenusByStore(UUID storeId) {
        // 가게 존재 확인, 활성 상태 메뉴만 조회, DTO 변환
        List<Menu> menus = menuRepository.findByStoreIdAndDeletedFalseOrderByCreatedAtDesc(storeId);

        return menus.stream()
                .map(menu -> MenuResponseDTO.MenuListResponseDTO.builder()
                        .menuId(menu.getId()) // .id() → .menuId()로 수정
                        .name(menu.getName())
                        .price(menu.getPrice())
                        .menuPicture(menu.getMenuPicture())
                        .status(menu.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    // 기존 메서드 호환성을 위한 오버로드 (기본 페이지네이션)
    public List<MenuResponseDTO.MenuListResponseDTO> getMenusByStore(UUID storeId, int page, int size) {
        // 페이지네이션을 적용하여 메뉴 조회
        List<Menu> menus = menuRepository.findByStoreIdAndDeletedFalseOrderByCreatedAtDesc(
                storeId, PageRequest.of(page, size));

        return menus.stream()
                .map(menu -> MenuResponseDTO.MenuListResponseDTO.builder()
                        .menuId(menu.getId())
                        .name(menu.getName())
                        .price(menu.getPrice())
                        .menuPicture(menu.getMenuPicture())
                        .status(menu.getStatus())
                        .category(menu.getMenuCategory().getCategory())
                        .build())
                .collect(Collectors.toList());
    }

    // 전체 인기 메뉴 TOP20 조회
    public List<MenuResponseDTO.MenuTopResponseDTO> getTopMenus() {
        List<Menu> topMenus = menuRepository.findTopMenusByOrderCount(PageRequest.of(0, 20));

        return topMenus.stream()
                .map(menu -> MenuResponseDTO.MenuTopResponseDTO.builder()
                        .menuId(menu.getId())
                        .name(menu.getName())
                        .price(menu.getPrice())
                        .menuPicture(menu.getMenuPicture())
                        .storeName(menu.getStore().getName()) // Store의 getName() 메서드 사용
                        .build())
                .collect(Collectors.toList());
    }

    // 시간대별 인기 메뉴 TOP20 조회
    public List<MenuResponseDTO.MenuTimeTopResponseDTO> getTimeTopMenus() {
        LocalDateTime startTime = LocalDateTime.now().minusHours(24); // 최근 24시간
        LocalDateTime endTime = LocalDateTime.now();

        List<Menu> timeTopMenus = menuRepository.findTopMenusByTimeRange(
                startTime, endTime, PageRequest.of(0, 20));

        return timeTopMenus.stream()
                .map(menu -> MenuResponseDTO.MenuTimeTopResponseDTO.builder()
                        .menuId(menu.getId())
                        .name(menu.getName())
                        .price(menu.getPrice())
                        .menuPicture(menu.getMenuPicture())
                        .storeName(menu.getStore().getName()) // Store의 getName() 메서드 사용
                        .orderCount(menu.getOrderItems().size()) // Mock에서는 단순하게
                        .build())
                .collect(Collectors.toList());
    }

    // 지역별 인기 메뉴 TOP20 조회
    public List<MenuResponseDTO.MenuRegionTopResponseDTO> getRegionTopMenus() {
        String region = "서울"; // 기본값 설정
        List<Menu> regionTopMenus = menuRepository.findTopMenusByRegion(region, PageRequest.of(0, 20));

        return regionTopMenus.stream()
                .map(menu -> MenuResponseDTO.MenuRegionTopResponseDTO.builder()
                        .menuId(menu.getId())
                        .name(menu.getName())
                        .price(menu.getPrice())
                        .menuPicture(menu.getMenuPicture())
                        .storeName(menu.getStore().getName()) // Store의 getName() 메서드 사용
                        .region(region)
                        .build())
                .collect(Collectors.toList());
    }

    // 메뉴 상세 조회
    public MenuResponseDTO.MenuDetailResponseDTO getMenuDetail(UUID menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuException(MenuErrorCode.NOT_FOUND));

        return MenuResponseDTO.MenuDetailResponseDTO.builder()
                .menuId(menu.getId())
                .name(menu.getName())
                .content(menu.getContent())
                .price(menu.getPrice())
                .menuPicture(menu.getMenuPicture())
                .status(menu.getStatus())
                .storeId(menu.getStore().getId())
                .storeName(menu.getStore().getName()) // Store의 getName() 메서드 사용
                .category(menu.getMenuCategory().getCategory())
                .createdAt(menu.getCreatedAt())
                .updatedAt(menu.getUpdatedAt())
                .build();
    }

    // 커서 기반 페이지네이션을 사용한 가게별 메뉴 조회
    public MenuResponseDTO.MenuStoreListResponseDTO getMenusByStoreWithCursor(UUID storeId, LocalDateTime cursor, Integer size) {
        if (cursor == null) {
            cursor = first_cursor;
        }

        Pageable pageable = PageRequest.of(0, size);
        Slice<Menu> menuSlice = menuRepository.findByStoreIdAndDeletedFalseAndCreatedAtBefore(storeId, cursor, pageable);

        if (menuSlice.isEmpty()) {
            throw new MenuException(MenuErrorCode.NOT_FOUND);
        }

        List<Menu> menuList = menuSlice.getContent();
        List<MenuResponseDTO.MenuListResponseDTO> menuDTOS = menuList.stream()
                .map(menu -> MenuResponseDTO.MenuListResponseDTO.builder()
                        .menuId(menu.getId())
                        .name(menu.getName())
                        .price(menu.getPrice())
                        .menuPicture(menu.getMenuPicture())
                        .status(menu.getStatus())
                        .category(menu.getMenuCategory().getCategory())
                        .build())
                .collect(Collectors.toList());

        LocalDateTime next_cursor = null;
        if (!menuList.isEmpty() && menuSlice.hasNext()) {
            next_cursor = menuList.getLast().getCreatedAt();
        }

        return MenuResponseDTO.MenuStoreListResponseDTO.builder()
                .menus(menuDTOS)
                .hasNext(menuSlice.hasNext())
                .nextCursor(next_cursor)
                .build();
    }
}
