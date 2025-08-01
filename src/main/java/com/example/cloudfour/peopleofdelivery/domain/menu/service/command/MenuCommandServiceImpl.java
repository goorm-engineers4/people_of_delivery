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

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MenuCommandServiceImpl {

    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;
    private final MenuCategoryRepository menuCategoryRepository;

    public MenuResponseDTO.MenuDetailResponseDTO createMenu(MenuRequestDTO.MenuCreateRequestDTO requestDTO, User user) {
        Store store = storeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new MenuException(MenuErrorCode.NOT_FOUND));

        MenuCategory menuCategory = menuCategoryRepository.findByCategory(requestDTO.getCategory())
                .orElseGet(() -> {
                    MenuCategory newCategory = MenuCategory.builder()
                            .category(requestDTO.getCategory())
                            .build();
                    return menuCategoryRepository.save(newCategory);
                });

        if (menuRepository.existsByNameAndStoreId(requestDTO.getName(), store.getId())) {
            throw new MenuException(MenuErrorCode.ALREADY_ADD);
        }

        Menu menu = Menu.builder()
                .name(requestDTO.getName())
                .content(requestDTO.getContent())
                .price(requestDTO.getPrice())
                .menuPicture(requestDTO.getMenuPicture())
                .status(requestDTO.getStatus())
                .build();

        menu.setStore(store);
        menu.setMenuCategory(menuCategory);

        Menu savedMenu = menuRepository.save(menu);

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

    public MenuResponseDTO.MenuDetailResponseDTO updateMenu(UUID menuId, MenuRequestDTO.MenuUpdateRequestDTO requestDTO, User user) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuException(MenuErrorCode.NOT_FOUND));

        if (!menu.getStore().getUser().getId().equals(user.getId())) {
            throw new MenuException(MenuErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (requestDTO.getName() != null || requestDTO.getContent() != null ||
            requestDTO.getPrice() != null || requestDTO.getMenuPicture() != null) {

            if (requestDTO.getName() != null && !menu.getName().equals(requestDTO.getName()) &&
                menuRepository.existsByNameAndStoreId(requestDTO.getName(), menu.getStore().getId())) {
                throw new MenuException(MenuErrorCode.ALREADY_ADD);
            }

            menu.updateMenuInfo(
                requestDTO.getName() != null ? requestDTO.getName() : menu.getName(),
                requestDTO.getContent() != null ? requestDTO.getContent() : menu.getContent(),
                requestDTO.getPrice() != null ? requestDTO.getPrice() : menu.getPrice(),
                requestDTO.getMenuPicture() != null ? requestDTO.getMenuPicture() : menu.getMenuPicture()
            );
        }

        if (requestDTO.getStatus() != null) {
            menu.updateStatus(requestDTO.getStatus());
        }

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

    public void deleteMenu(UUID menuId, User user) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuException(MenuErrorCode.NOT_FOUND));

        if (!menu.getStore().getUser().getId().equals(user.getId())) {
            throw new MenuException(MenuErrorCode.UNAUTHORIZED_ACCESS);
        }

        menuRepository.delete(menu);
        log.info("메뉴 ID: {}가 삭제되었습니다.", menuId);
    }
}
