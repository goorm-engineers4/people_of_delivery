package com.example.cloudfour.peopleofdelivery.domain.menu.service.query;

import com.example.cloudfour.peopleofdelivery.domain.menu.converter.MenuConverter;
import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.Menu;
import com.example.cloudfour.peopleofdelivery.domain.menu.exception.MenuErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.menu.exception.MenuException;
import com.example.cloudfour.peopleofdelivery.domain.menu.repository.MenuRepository;
import com.example.cloudfour.peopleofdelivery.domain.store.exception.StoreErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.store.exception.StoreException;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuQueryServiceImpl {
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

    public List<MenuResponseDTO.MenuListResponseDTO> getMenusByStore(UUID storeId) {
        // 가게 존재 여부 검증
        storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.NOT_FOUND));

        // Repository 구현 후 활성화 예정
        /*
        List<Menu> menus = menuRepository.findByStoreId(storeId);
        */

        // 임시: 모든 메뉴 조회 (Repository 구현 전까지)
        List<Menu> menus = menuRepository.findAll();

        log.info("가게별 메뉴 조회 완��� - storeId: {}, 메뉴 개수: {}", storeId, menus.size());

        return menus.stream()
                .map(MenuConverter::toMenuListResponseDTO)
                .collect(Collectors.toList());
    }

    public List<MenuResponseDTO.MenuTopResponseDTO> getTopMenus() {
        // Repository 구현 후 활성화 예정
        /*
        List<Menu> menus = menuRepository.findTop20ByOrderByPopularityDesc();
        */

        // 임시: 모든 메뉴 조회 후 20개 제한 (Repository 구현 전까지)
        List<Menu> menus = menuRepository.findAll();

        log.info("인기 메뉴 TOP20 조회 완료 - 메뉴 개수: {}", menus.size());

        return menus.stream()
                .limit(20)
                .map(MenuConverter::toMenuTopResponseDTO)
                .collect(Collectors.toList());
    }

    public List<MenuResponseDTO.MenuTimeTopResponseDTO> getTimeTopMenus() {
        // Repository 구현 후 활성화 예정
        /*
        List<Menu> menus = menuRepository.findTop20ByTimePopularity();
        */

        // 임시: 모든 메뉴 조회 후 20개 제한 (Repository 구현 전까지)
        List<Menu> menus = menuRepository.findAll();

        log.info("시간대별 인기 메뉴 TOP20 조회 완료 - 메뉴 개수: {}", menus.size());

        return menus.stream()
                .limit(20)
                .map(MenuConverter::toMenuTimeTopResponseDTO)
                .collect(Collectors.toList());
    }

    public List<MenuResponseDTO.MenuRegionTopResponseDTO> getRegionTopMenus() {
        // Repository 구현 후 활성화 예정
        /*
        List<Menu> menus = menuRepository.findTop20ByRegionPopularity();
        */

        // 임시: 모든 메뉴 조회 후 20개 제한 (Repository 구현 전까지)
        List<Menu> menus = menuRepository.findAll();

        log.info("지역별 인기 메뉴 TOP20 조회 완료 - 메뉴 개수: {}", menus.size());

        return menus.stream()
                .limit(20)
                .map(MenuConverter::toMenuRegionTopResponseDTO)
                .collect(Collectors.toList());
    }

    public MenuResponseDTO.MenuDetailResponseDTO getMenuDetail(UUID menuId) {
        Menu findMenu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuException(MenuErrorCode.NOT_FOUND));

        log.info("메뉴 상세 조회 완료 - menuId: {}, name: {}", findMenu.getId(), findMenu.getName());

        return MenuConverter.toMenuDetailResponseDTO(findMenu);
    }
}
