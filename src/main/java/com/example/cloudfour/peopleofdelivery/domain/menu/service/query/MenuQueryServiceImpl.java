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

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuQueryServiceImpl {

    private final MenuRepository menuRepository;
    private static final LocalDateTime first_cursor = LocalDateTime.now().plusDays(1);

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

    public List<MenuResponseDTO.MenuTopResponseDTO> getTopMenus() {
        List<Menu> topMenus = menuRepository.findTopMenusByOrderCount(PageRequest.of(0, 20));

        return topMenus.stream()
                .map(menu -> MenuResponseDTO.MenuTopResponseDTO.builder()
                        .menuId(menu.getId())
                        .name(menu.getName())
                        .price(menu.getPrice())
                        .menuPicture(menu.getMenuPicture())
                        .storeName(menu.getStore().getName())
                        .build())
                .collect(Collectors.toList());
    }

    public List<MenuResponseDTO.MenuTimeTopResponseDTO> getTimeTopMenus() {
        LocalDateTime startTime = LocalDateTime.now().minusHours(24);
        LocalDateTime endTime = LocalDateTime.now();

        List<Menu> timeTopMenus = menuRepository.findTopMenusByTimeRange(
                startTime, endTime, PageRequest.of(0, 20));

        return timeTopMenus.stream()
                .map(menu -> MenuResponseDTO.MenuTimeTopResponseDTO.builder()
                        .menuId(menu.getId())
                        .name(menu.getName())
                        .price(menu.getPrice())
                        .menuPicture(menu.getMenuPicture())
                        .storeName(menu.getStore().getName())
                        .orderCount(menu.getOrderItems().size())
                        .build())
                .collect(Collectors.toList());
    }

    public List<MenuResponseDTO.MenuRegionTopResponseDTO> getRegionTopMenus(String si, String gu) {
        List<Menu> regionTopMenus = menuRepository.findTopMenusByRegion(si, gu, PageRequest.of(0, 20));

        return regionTopMenus.stream()
                .map(menu -> MenuResponseDTO.MenuRegionTopResponseDTO.builder()
                        .menuId(menu.getId())
                        .name(menu.getName())
                        .price(menu.getPrice())
                        .menuPicture(menu.getMenuPicture())
                        .status(menu.getStatus())
                        .category(menu.getMenuCategory().getCategory())
                        .storeName(menu.getStore().getName())
                        .region(menu.getStore().getRegion().getSi() + " " + menu.getStore().getRegion().getGu())
                        .build())
                .collect(Collectors.toList());
    }

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
                .storeName(menu.getStore().getName())
                .category(menu.getMenuCategory().getCategory())
                .createdAt(menu.getCreatedAt())
                .updatedAt(menu.getUpdatedAt())
                .build();
    }
}
