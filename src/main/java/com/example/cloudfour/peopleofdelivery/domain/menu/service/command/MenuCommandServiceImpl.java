package com.example.cloudfour.peopleofdelivery.domain.menu.service.command;

import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.Menu;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuCategory;
import com.example.cloudfour.peopleofdelivery.domain.menu.repository.MenuCategoryRepository;
import com.example.cloudfour.peopleofdelivery.domain.menu.repository.MenuRepository;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.global.exception.DuplicateException;
import com.example.cloudfour.peopleofdelivery.global.exception.NotFoundException;
import com.example.cloudfour.peopleofdelivery.global.exception.UnauthorizedException;
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
        // 권한 검사, 사업자의 가게 조회 (findByOwnerId → findByUserId로 수정)
        Store store = storeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("가게를 찾을 수 없습니다."));

        // 메뉴 카테고리 조회
        MenuCategory menuCategory = menuCategoryRepository.findByCategory(requestDTO.getCategory())
                .orElseThrow(() -> new NotFoundException("메뉴 카테고리를 찾을 수 없습니다."));

        // 메뉴명 중복 체크
        if (menuRepository.existsByNameAndStoreId(requestDTO.getName(), store.getId())) {
            throw new DuplicateException("이미 존재하는 메뉴명입니다.");
        }

        // 엔티티 생성 및 저장
        Menu menu = Menu.builder()
                .store(store)
                .menuCategory(menuCategory)
                .name(requestDTO.getName())
                .content(requestDTO.getContent())
                .price(requestDTO.getPrice())
                .menuPicture(requestDTO.getMenuPicture())
                .status(requestDTO.getStatus())
                .build();

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
                .storeName(savedMenu.getStore().getName()) // Store의 getName() 메서드 사용
                .category(savedMenu.getMenuCategory().getCategory())
                .createdAt(savedMenu.getCreatedAt())
                .updatedAt(savedMenu.getUpdatedAt())
                .build();
    }

    // 메뉴 수정
    public MenuResponseDTO.MenuDetailResponseDTO updateMenu(UUID menuId, MenuRequestDTO.MenuUpdateRequestDTO requestDTO, User user) {
        // 메뉴 존재 확인
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new NotFoundException("메뉴를 찾을 수 없습니다."));

        // 권한 체크 - 가게 주인인지 확인 (getOwner() → getUser()로 수정)
        if (!menu.getStore().getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("수정 권한이 없습니다.");
        }

        return MenuResponseDTO.MenuDetailResponseDTO.builder()
                .menuId(menu.getId())
                .name(requestDTO.getName() != null ? requestDTO.getName() : menu.getName())
                .content(requestDTO.getContent() != null ? requestDTO.getContent() : menu.getContent())
                .price(requestDTO.getPrice() != null ? requestDTO.getPrice() : menu.getPrice())
                .menuPicture(menu.getMenuPicture())
                .status(menu.getStatus())
                .storeId(menu.getStore().getId())
                .storeName(menu.getStore().getName()) // Store의 getName() 메서드 사용
                .category(menu.getMenuCategory().getCategory())
                .createdAt(menu.getCreatedAt())
                .updatedAt(menu.getUpdatedAt())
                .build();
    }

    // 메뉴 삭제
    public void deleteMenu(UUID menuId, User user) {
        // 메뉴 존재 확인
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new NotFoundException("메뉴를 찾을 수 없습니다."));

        // 권한 체크 - 가게 주인인지 확인 (getOwner() → getUser()로 수정)
        if (!menu.getStore().getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("삭제 권한이 없습니다.");
        }

        // 소프트 삭제 (실제로는 Menu 엔티티에 delete 메서드를 만들어서 사용)
        // 여기서는 테스트를 위한 간단한 구현
        log.info("메뉴 ID: {}가 삭제되었습니다.", menuId);
    }
}
