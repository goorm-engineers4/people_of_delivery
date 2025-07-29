package com.example.cloudfour.peopleofdelivery.domain.menu.service.command;

import com.example.cloudfour.peopleofdelivery.domain.menu.converter.MenuConverter;
import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.Menu;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuCategory;
import com.example.cloudfour.peopleofdelivery.domain.menu.exception.MenuErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.menu.exception.MenuException;
import com.example.cloudfour.peopleofdelivery.domain.menu.repository.MenuRepository;
import com.example.cloudfour.peopleofdelivery.domain.menu.repository.MenuCategoryRepository;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.store.exception.StoreErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.store.exception.StoreException;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.domain.user.exception.UserErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.user.exception.UserException;
import com.example.cloudfour.peopleofdelivery.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MenuCommandServiceImpl {
    private final MenuRepository menuRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    public MenuResponseDTO.MenuDetailResponseDTO createMenu(MenuRequestDTO.MenuCreateRequestDTO requestDTO, User user) {
        // 사용자 검증
        userRepository.findById(user.getId()).orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));

        // Repository 구현 후 활성화 예정
        /*
        Store userStore = storeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new StoreException(StoreErrorCode.NOT_FOUND));
        */

        // 임시: 첫 번째 가게를 사용 (Repository 구현 전까지)
        List<Store> stores = storeRepository.findAll();
        Store userStore = stores.isEmpty() ? null : stores.get(0);
        if (userStore == null) {
            throw new StoreException(StoreErrorCode.NOT_FOUND);
        }

        // Repository 구현 후 활성화 예정
        /*
        MenuCategory findMenuCategory = null;
        if (requestDTO.getCategory() != null) {
            findMenuCategory = menuCategoryRepository.findByCategory(requestDTO.getCategory())
                    .orElse(null);
        }
        */

        // 임시: 첫 번째 카테고리를 사용 (Repository 구현 전까지)
        MenuCategory findMenuCategory = null;
        if (requestDTO.getCategory() != null) {
            List<MenuCategory> categories = menuCategoryRepository.findAll();
            findMenuCategory = categories.isEmpty() ? null : categories.get(0);
        }

        // Menu 엔티티 생성
        Menu menu = MenuConverter.toMenu(requestDTO);
        menu.setStore(userStore);
        if (findMenuCategory != null) {
            menu.setMenuCategory(findMenuCategory);
        }

        menuRepository.save(menu);

        log.info("메뉴 생성 완료 - menuId: {}, name: {}, storeId: {}", menu.getId(), menu.getName(), userStore.getId());

        return MenuConverter.toMenuDetailResponseDTO(menu);
    }

    public MenuResponseDTO.MenuDetailResponseDTO updateMenu(UUID menuId, MenuRequestDTO.MenuUpdateRequestDTO requestDTO, User user) {
        // 사용자 검증
        userRepository.findById(user.getId()).orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));

        // 메뉴 조회 및 검증
        Menu findMenu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuException(MenuErrorCode.NOT_FOUND));

        // 메뉴 소유자 검증 (가게 소유자인지 확인)
        if (!findMenu.getStore().getUser().getId().equals(user.getId())) {
            throw new MenuException(MenuErrorCode.UNAUTHORIZED_ACCESS);
        }

        // 메뉴 카테고리 변경이 있는 경우 처리
        if (requestDTO.getCategory() != null) {
            // Repository 구현 후 활성화 예정
            /*
            MenuCategory newMenuCategory = menuCategoryRepository.findByCategory(requestDTO.getCategory())
                    .orElse(null);
            */

            // 임시: 첫 번째 카테고리를 사용 (Repository 구현 전까지)
            List<MenuCategory> categories = menuCategoryRepository.findAll();
            MenuCategory newMenuCategory = categories.isEmpty() ? null : categories.get(0);
            findMenu.setMenuCategory(newMenuCategory);
        }

        // 메뉴 정보 업데이트 (엔티티에 update 메서드가 있다고 가정)
        findMenu.update(
                requestDTO.getName(),
                requestDTO.getContent(),
                requestDTO.getPrice(),
                requestDTO.getMenuPicture(),
                requestDTO.getStatus()
        );

        menuRepository.save(findMenu);

        log.info("메뉴 수정 완료 - menuId: {}, name: {}", findMenu.getId(), findMenu.getName());

        return MenuConverter.toMenuDetailResponseDTO(findMenu);
    }

    public void deleteMenu(UUID menuId, User user) {
        // 사용자 검증
        userRepository.findById(user.getId()).orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));

        // 메뉴 조회 및 검증
        Menu findMenu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuException(MenuErrorCode.NOT_FOUND));

        // 메뉴 소유자 검증 (가게 소유자인지 확인)
        if (!findMenu.getStore().getUser().getId().equals(user.getId())) {
            throw new MenuException(MenuErrorCode.UNAUTHORIZED_ACCESS);
        }

        // 소프트 삭제 (엔티티에 softDelete 메서드가 있다고 가정)
        findMenu.softDelete();

        log.info("메뉴 삭제 완료 - menuId: {}, name: {}", findMenu.getId(), findMenu.getName());
    }
}
