package com.example.cloudfour.peopleofdelivery.domain.menu.service.command;

import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.Menu;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuCategory;
import com.example.cloudfour.peopleofdelivery.domain.menu.exception.MenuException;
import com.example.cloudfour.peopleofdelivery.domain.menu.exception.MenuErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.menu.repository.MenuCategoryRepository;
import com.example.cloudfour.peopleofdelivery.domain.menu.repository.MenuRepository;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service // 서비스 레이어 어노테이션
@Slf4j // 로그 찍기 기능
@RequiredArgsConstructor // 생성자 주입을 위한 어노테이션
@Transactional // 트랜잭션 관리 어노테이션
public class MenuCommandServiceImpl {

    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;
    private final MenuCategoryRepository menuCategoryRepository;

    // 메뉴 생성
    public MenuResponseDTO.MenuDetailResponseDTO createMenu(MenuRequestDTO.MenuCreateRequestDTO requestDTO, User user) {
        // 권한 검사, 사업자의 가게 조회
        Store store = storeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new MenuException(MenuErrorCode.NOT_FOUND));

        // 메뉴 카테고리 조회 또는 생성
        MenuCategory menuCategory = menuCategoryRepository.findByCategory(requestDTO.getCategory())
                .orElseGet(() -> {
                    // 카테고리가 없으면 새로 생성
                    MenuCategory newCategory = MenuCategory.builder()
                            .category(requestDTO.getCategory())
                            .build();
                    return menuCategoryRepository.save(newCategory);
                });

        // 메뉴명 중복 체크
        if (menuRepository.existsByNameAndStoreId(requestDTO.getName(), store.getId())) {
            throw new MenuException(MenuErrorCode.ALREADY_ADD);
        }

        // 엔티티 생성 및 저장
        Menu menu = Menu.builder()
                .name(requestDTO.getName())
                .content(requestDTO.getContent())
                .price(requestDTO.getPrice())
                .menuPicture(requestDTO.getMenuPicture())
                .status(requestDTO.getStatus())
                .build();

        // 연관관계 설정 메서드 사용
        menu.setStore(store);
        menu.setMenuCategory(menuCategory);

        Menu savedMenu = menuRepository.save(menu);

        // 응답 DTO 생성
        return MenuResponseDTO.MenuDetailResponseDTO.builder()
                .menuId(savedMenu.getId())
                .name(savedMenu.getName())
                .content(savedMenu.getContent())
                .price(savedMenu.getPrice())
                .menuPicture(savedMenu.getMenuPicture())
                .status(savedMenu.getStatus())
                .storeId(savedMenu.getStore().getId())
                .storeName(savedMenu.getStore().getName())
                .category(savedMenu.getMenuCategory().getCategory())
                .createdAt(savedMenu.getCreatedAt())
                .updatedAt(savedMenu.getUpdatedAt())
                .build();
    }

    // 메뉴 수정
    public MenuResponseDTO.MenuDetailResponseDTO updateMenu(UUID menuId, MenuRequestDTO.MenuUpdateRequestDTO requestDTO, User user) {
        // 메뉴 존재 확인
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuException(MenuErrorCode.NOT_FOUND));

        // 권한 체크 - 가게 주인인지 확인
        if (!menu.getStore().getUser().getId().equals(user.getId())) {
            throw new MenuException(MenuErrorCode.UNAUTHORIZED_ACCESS);
        }

        // 메뉴 정보 업데이트 - updateMenuInfo 메서드 사용
        if (requestDTO.getName() != null || requestDTO.getContent() != null ||
            requestDTO.getPrice() != null || requestDTO.getMenuPicture() != null) {

            // 메뉴명 중복 체크 (자기 자신 제외)
            if (requestDTO.getName() != null && !menu.getName().equals(requestDTO.getName()) &&
                menuRepository.existsByNameAndStoreId(requestDTO.getName(), menu.getStore().getId())) {
                throw new MenuException(MenuErrorCode.ALREADY_ADD);
            }

            // updateMenuInfo 메서드 사용하여 일괄 업데이트
            menu.updateMenuInfo(
                requestDTO.getName() != null ? requestDTO.getName() : menu.getName(),
                requestDTO.getContent() != null ? requestDTO.getContent() : menu.getContent(),
                requestDTO.getPrice() != null ? requestDTO.getPrice() : menu.getPrice(),
                requestDTO.getMenuPicture() != null ? requestDTO.getMenuPicture() : menu.getMenuPicture()
            );
        }

        // 상태 변경이 있으면 updateStatus 메서드 사용
        if (requestDTO.getStatus() != null) {
            menu.updateStatus(requestDTO.getStatus());
        }

        // 변경된 메뉴 저장
        Menu updatedMenu = menuRepository.save(menu);

        return MenuResponseDTO.MenuDetailResponseDTO.builder()
                .menuId(updatedMenu.getId())
                .name(updatedMenu.getName())
                .content(updatedMenu.getContent())
                .price(updatedMenu.getPrice())
                .menuPicture(updatedMenu.getMenuPicture())
                .status(updatedMenu.getStatus())
                .storeId(updatedMenu.getStore().getId())
                .storeName(updatedMenu.getStore().getName())
                .category(updatedMenu.getMenuCategory().getCategory())
                .createdAt(updatedMenu.getCreatedAt())
                .updatedAt(updatedMenu.getUpdatedAt())
                .build();
    }

    // 메뉴 삭제
    public void deleteMenu(UUID menuId, User user) {
        // 메뉴 존재 확인
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuException(MenuErrorCode.NOT_FOUND));

        // 권한 체크 - 가게 주인인지 확인
        if (!menu.getStore().getUser().getId().equals(user.getId())) {
            throw new MenuException(MenuErrorCode.UNAUTHORIZED_ACCESS);
        }

        // 실제 메뉴 삭제
        menuRepository.delete(menu);
        log.info("메뉴 ID: {}가 삭제되었습니다.", menuId);
    }
}
