package com.example.cloudfour.peopleofdelivery.domain.menu.service.command;

import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuOptionResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.Menu;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuCategory;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuOption;
import com.example.cloudfour.peopleofdelivery.domain.menu.exception.MenuException;
import com.example.cloudfour.peopleofdelivery.domain.menu.exception.MenuErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.menu.repository.MenuCategoryRepository;
import com.example.cloudfour.peopleofdelivery.domain.menu.repository.MenuRepository;
import com.example.cloudfour.peopleofdelivery.domain.menu.repository.MenuOptionRepository;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.store.exception.StoreErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.store.exception.StoreException;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.Role;
import com.example.cloudfour.peopleofdelivery.global.auth.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MenuCommandServiceImpl {

    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final MenuOptionRepository menuOptionRepository;

    public MenuResponseDTO.MenuDetailResponseDTO createMenu(MenuRequestDTO.MenuCreateRequestDTO requestDTO, UUID storeId,CustomUserDetails userDetails) {
        Store store = storeRepository.findByIdAndIsDeletedFalse(storeId).orElseThrow(() -> new StoreException(StoreErrorCode.NOT_FOUND));
        if (!(userDetails.getRole() == Role.MASTER || userDetails.getRole() == Role.OWNER)) {
            throw new MenuException(MenuErrorCode.UNAUTHORIZED_ACCESS);
        }
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

        List<MenuOption> menuOptions = createMenuOptions(requestDTO.getMenuOptions(), savedMenu);

        List<MenuResponseDTO.MenuOptionDTO> menuOptionDTOs = menuOptions.stream()
                .map(option -> MenuResponseDTO.MenuOptionDTO.builder()
                        .menuOptionId(option.getId())
                        .optionName(option.getOptionName())
                        .additionalPrice(option.getAdditionalPrice())
                        .build())
                .collect(Collectors.toList());

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
                .menuOptions(menuOptionDTOs)
                .build();
    }

    public MenuResponseDTO.MenuDetailResponseDTO updateMenu(UUID menuId, MenuRequestDTO.MenuUpdateRequestDTO requestDTO, CustomUserDetails userDetails) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuException(MenuErrorCode.NOT_FOUND));

        if (!menu.getStore().getUser().getId().equals(userDetails.getId())) {
            throw new MenuException(MenuErrorCode.UNAUTHORIZED_ACCESS);
        }

        MenuCategory menuCategory = menuCategoryRepository.findByCategory(requestDTO.getCategory())
                .orElseGet(() -> {
                    MenuCategory newCategory = MenuCategory.builder()
                            .category(requestDTO.getCategory())
                            .build();
                    return menuCategoryRepository.save(newCategory);
                });
        menu.updateMenuInfo(requestDTO.getName(),requestDTO.getContent(),
                requestDTO.getPrice(),requestDTO.getMenuPicture(),requestDTO.getStatus());
        menu.setMenuCategory(menuCategory);
        Menu updatedMenu = menuRepository.save(menu);

        List<MenuOption> existingOptions = menuOptionRepository.findByMenuIdOrderByAdditionalPrice(menuId);
        menuOptionRepository.deleteAll(existingOptions);

        List<MenuOption> menuOptions = createMenuOptions(requestDTO.getMenuOptions(), updatedMenu);

        List<MenuResponseDTO.MenuOptionDTO> menuOptionDTOs = menuOptions.stream()
                .map(option -> MenuResponseDTO.MenuOptionDTO.builder()
                        .menuOptionId(option.getId())
                        .optionName(option.getOptionName())
                        .additionalPrice(option.getAdditionalPrice())
                        .build())
                .collect(Collectors.toList());

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
                .menuOptions(menuOptionDTOs)
                .build();
    }

    public void deleteMenu(UUID menuId, CustomUserDetails userDetails) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuException(MenuErrorCode.NOT_FOUND));

        if (!menu.getStore().getUser().getId().equals(userDetails.getId())) {
            throw new MenuException(MenuErrorCode.UNAUTHORIZED_ACCESS);
        }

        menuRepository.delete(menu);
        log.info("메뉴 ID: {}가 삭제되었습니다.", menuId);
    }

    private List<MenuOption> createMenuOptions(List<MenuRequestDTO.MenuOptionCreateRequestDTO> optionDTOs, Menu menu) {
        if (optionDTOs == null || optionDTOs.isEmpty()) {
            return List.of();
        }

        return optionDTOs.stream()
                .map(optionDTO -> {
                    if (menuOptionRepository.existsByMenuIdAndOptionName(menu.getId(), optionDTO.getOptionName())) {
                        throw new MenuException(MenuErrorCode.ALREADY_ADD);
                    }

                    MenuOption menuOption = MenuOption.builder()
                            .optionName(optionDTO.getOptionName())
                            .additionalPrice(optionDTO.getAdditionalPrice())
                            .build();

                    menuOption.setMenu(menu);
                    return menuOptionRepository.save(menuOption);
                })
                .collect(Collectors.toList());
    }

    public MenuOptionResponseDTO.MenuOptionDetailResponseDTO createMenuOption(
            MenuRequestDTO.MenuOptionStandaloneCreateRequestDTO requestDTO, CustomUserDetails userDetails) {

        Menu menu = menuRepository.findById(requestDTO.getMenuId())
                .orElseThrow(() -> new MenuException(MenuErrorCode.NOT_FOUND));

        if (!(userDetails.getRole() == Role.MASTER || userDetails.getRole() == Role.OWNER)) {
            throw new MenuException(MenuErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (userDetails.getRole() == Role.OWNER &&
            !menu.getStore().getUser().getId().equals(userDetails.getId())) {
            throw new MenuException(MenuErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (menuOptionRepository.existsByMenuIdAndOptionName(menu.getId(), requestDTO.getOptionName())) {
            throw new MenuException(MenuErrorCode.ALREADY_ADD);
        }

        MenuOption menuOption = MenuOption.builder()
                .optionName(requestDTO.getOptionName())
                .additionalPrice(requestDTO.getAdditionalPrice())
                .build();

        menuOption.setMenu(menu);
        MenuOption savedOption = menuOptionRepository.save(menuOption);

        return MenuOptionResponseDTO.MenuOptionDetailResponseDTO.builder()
                .menuOptionId(savedOption.getId())
                .menuId(menu.getId())
                .menuName(menu.getName())
                .storeName(menu.getStore().getName())
                .optionName(savedOption.getOptionName())
                .additionalPrice(savedOption.getAdditionalPrice())
                .createdAt(menu.getCreatedAt())
                .updatedAt(menu.getUpdatedAt())
                .build();
    }

    public MenuOptionResponseDTO.MenuOptionDetailResponseDTO updateMenuOption(
            UUID optionId, MenuRequestDTO.MenuOptionStandaloneUpdateRequestDTO requestDTO, CustomUserDetails userDetails) {

        MenuOption menuOption = menuOptionRepository.findByIdWithMenu(optionId)
                .orElseThrow(() -> new MenuException(MenuErrorCode.NOT_FOUND));

        if (!(userDetails.getRole() == Role.MASTER || userDetails.getRole() == Role.OWNER)) {
            throw new MenuException(MenuErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (userDetails.getRole() == Role.OWNER &&
            !menuOption.getMenu().getStore().getUser().getId().equals(userDetails.getId())) {
            throw new MenuException(MenuErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (!menuOption.getOptionName().equals(requestDTO.getOptionName()) &&
            menuOptionRepository.existsByMenuIdAndOptionName(menuOption.getMenu().getId(), requestDTO.getOptionName())) {
            throw new MenuException(MenuErrorCode.ALREADY_ADD);
        }


        menuOption.updateOptionInfo(requestDTO.getOptionName(), requestDTO.getAdditionalPrice());
        MenuOption savedOption = menuOptionRepository.save(menuOption);

        return MenuOptionResponseDTO.MenuOptionDetailResponseDTO.builder()
                .menuOptionId(savedOption.getId())
                .menuId(savedOption.getMenu().getId())
                .menuName(savedOption.getMenu().getName())
                .storeName(savedOption.getMenu().getStore().getName())
                .optionName(savedOption.getOptionName())
                .additionalPrice(savedOption.getAdditionalPrice())
                .createdAt(savedOption.getMenu().getCreatedAt())
                .updatedAt(savedOption.getMenu().getUpdatedAt())
                .build();
    }

    public void deleteMenuOption(UUID optionId, CustomUserDetails userDetails) {
        MenuOption menuOption = menuOptionRepository.findByIdWithMenu(optionId)
                .orElseThrow(() -> new MenuException(MenuErrorCode.NOT_FOUND));

        if (!(userDetails.getRole() == Role.MASTER || userDetails.getRole() == Role.OWNER)) {
            throw new MenuException(MenuErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (userDetails.getRole() == Role.OWNER &&
            !menuOption.getMenu().getStore().getUser().getId().equals(userDetails.getId())) {
            throw new MenuException(MenuErrorCode.UNAUTHORIZED_ACCESS);
        }

        menuOptionRepository.delete(menuOption);
        log.info("메뉴 옵션 ID: {}가 삭제되었습니다.", optionId);
    }
}
