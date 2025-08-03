package com.example.cloudfour.peopleofdelivery.unit.domain.menu.service.command;

import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.Menu;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuCategory;
import com.example.cloudfour.peopleofdelivery.domain.menu.enums.MenuStatus;
import com.example.cloudfour.peopleofdelivery.domain.menu.exception.MenuException;
import com.example.cloudfour.peopleofdelivery.domain.menu.repository.MenuCategoryRepository;
import com.example.cloudfour.peopleofdelivery.domain.menu.repository.MenuOptionRepository;
import com.example.cloudfour.peopleofdelivery.domain.menu.repository.MenuRepository;
import com.example.cloudfour.peopleofdelivery.domain.menu.service.command.MenuCommandServiceImpl;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.store.exception.StoreException;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.Role;
import com.example.cloudfour.peopleofdelivery.fixtures.Factory;
import com.example.cloudfour.peopleofdelivery.global.auth.userdetails.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("메뉴 Command 서비스 테스트")
class MenuCommandServiceImplTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private MenuCategoryRepository menuCategoryRepository;

    @Mock
    private MenuOptionRepository menuOptionRepository;

    @InjectMocks
    private MenuCommandServiceImpl menuCommandService;

    private User testOwnerUser;
    private CustomUserDetails ownerUserDetails;

    @BeforeEach
    void setUp() {
        testOwnerUser = Factory.createMockUserWithRole(Role.OWNER);
        ownerUserDetails = createCustomUserDetails(testOwnerUser);
    }

    private CustomUserDetails createCustomUserDetails(User user) {
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(user.getId());
        when(userDetails.getRole()).thenReturn(user.getRole());
        return userDetails;
    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = {"OWNER", "MASTER"})
    @DisplayName("메뉴 생성 권한이 있는 역할 테스트 (OWNER, MASTER)")
    void createMenu_AuthorizedRoles_Success(Role role) {
        User authorizedUser = Factory.createMockUserWithRole(role);
        CustomUserDetails authorizedUserDetails = createCustomUserDetails(authorizedUser);

        UUID storeId = UUID.randomUUID();
        Store testStore = mock(Store.class);
        MenuCategory testMenuCategory = mock(MenuCategory.class);

        when(testStore.getId()).thenReturn(storeId);
        when(testStore.getName()).thenReturn("권한테스트가게_" + role.name());
        when(testMenuCategory.getCategory()).thenReturn("치킨류");

        MenuRequestDTO.MenuCreateRequestDTO requestDTO = MenuRequestDTO.MenuCreateRequestDTO.builder()
                .name("신메뉴_" + role.name())
                .content("새로운 BBQ 소스로 맛을 낸 치킨")
                .price(22000)
                .menuPicture("https://example.com/bbq-chicken.jpg")
                .status(MenuStatus.판매중)
                .category("치킨류")
                .build();

        Menu newMenu = mock(Menu.class);
        when(newMenu.getId()).thenReturn(UUID.randomUUID());
        when(newMenu.getName()).thenReturn("신메뉴_" + role.name());
        when(newMenu.getPrice()).thenReturn(22000);
        when(newMenu.getContent()).thenReturn("새로운 BBQ 소스로 맛을 낸 치킨");
        when(newMenu.getStatus()).thenReturn(MenuStatus.판매중);
        when(newMenu.getMenuPicture()).thenReturn("https://example.com/bbq-chicken.jpg");
        when(newMenu.getStore()).thenReturn(testStore);
        when(newMenu.getMenuCategory()).thenReturn(testMenuCategory);
        when(newMenu.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(newMenu.getUpdatedAt()).thenReturn(LocalDateTime.now());

        given(storeRepository.findByIdAndIsDeletedFalse(storeId))
                .willReturn(Optional.of(testStore));
        given(menuCategoryRepository.findByCategory("치킨류"))
                .willReturn(Optional.of(testMenuCategory));
        given(menuRepository.existsByNameAndStoreId(requestDTO.getName(), storeId))
                .willReturn(false);
        given(menuRepository.save(any(Menu.class)))
                .willReturn(newMenu);

        MenuResponseDTO.MenuDetailResponseDTO result = menuCommandService.createMenu(requestDTO, storeId, authorizedUserDetails);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("신메뉴_" + role.name());
        assertThat(result.getPrice()).isEqualTo(22000);
        assertThat(result.getStatus()).isEqualTo(MenuStatus.판매중);
        assertThat(result.getStoreId()).isEqualTo(storeId);
        assertThat(result.getCategory()).isEqualTo("치킨류");
    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = {"CUSTOMER", "RIDER"})
    @DisplayName("메뉴 생성 권한이 없는 역할 테스트 (CUSTOMER, RIDER)")
    void createMenu_UnauthorizedRoles_ThrowsException(Role role) {
        User unauthorizedUser = Factory.createMockUserWithRole(role);
        CustomUserDetails unauthorizedUserDetails = createCustomUserDetails(unauthorizedUser);

        UUID storeId = UUID.randomUUID();
        Store testStore = mock(Store.class);

        MenuRequestDTO.MenuCreateRequestDTO requestDTO = MenuRequestDTO.MenuCreateRequestDTO.builder()
                .name("권한없음테스트메뉴_" + role.name())
                .content("권한 없는 사용자 테스트")
                .price(15000)
                .category("테스트카테고리")
                .status(MenuStatus.판매중)
                .build();

        given(storeRepository.findByIdAndIsDeletedFalse(storeId))
                .willReturn(Optional.of(testStore));

        assertThatThrownBy(() -> menuCommandService.createMenu(requestDTO, storeId, unauthorizedUserDetails))
                .isInstanceOf(MenuException.class);
    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = {"OWNER", "MASTER"})
    @DisplayName("메뉴 수정 권한이 있는 역할 테스트 (OWNER, MASTER)")
    void updateMenu_AuthorizedRoles_Success(Role role) {
        User authorizedUser = Factory.createMockUserWithRole(role);
        CustomUserDetails authorizedUserDetails = createCustomUserDetails(authorizedUser);

        UUID menuId = UUID.randomUUID();
        Store testStore = mock(Store.class);
        MenuCategory testMenuCategory = mock(MenuCategory.class);
        Menu testMenu = mock(Menu.class);

        when(testStore.getUser()).thenReturn(authorizedUser);
        when(testMenu.getStore()).thenReturn(testStore);
        when(testMenu.getId()).thenReturn(menuId);
        when(testMenu.getName()).thenReturn("프리미엄 후라이드치킨_" + role.name());
        when(testMenu.getPrice()).thenReturn(20000);
        when(testMenu.getContent()).thenReturn("더욱 바삭해진 프리미엄 치킨");
        when(testMenu.getStatus()).thenReturn(MenuStatus.판매중);
        when(testMenu.getMenuCategory()).thenReturn(testMenuCategory);
        when(testMenu.getCreatedAt()).thenReturn(LocalDateTime.now().minusHours(1));
        when(testMenu.getUpdatedAt()).thenReturn(LocalDateTime.now());
        when(testMenuCategory.getCategory()).thenReturn("치킨류");

        MenuRequestDTO.MenuUpdateRequestDTO requestDTO = MenuRequestDTO.MenuUpdateRequestDTO.builder()
                .name("프리미엄 후라이드치킨_" + role.name())
                .price(20000)
                .content("더욱 바삭해진 프리미엄 치킨")
                .category("치킨류")
                .status(MenuStatus.판매중)
                .build();

        given(menuRepository.findById(menuId))
                .willReturn(Optional.of(testMenu));
        given(menuCategoryRepository.findByCategory("치킨류"))
                .willReturn(Optional.of(testMenuCategory));
        given(menuRepository.save(any(Menu.class)))
                .willReturn(testMenu);

        MenuResponseDTO.MenuDetailResponseDTO result = menuCommandService.updateMenu(menuId, requestDTO, authorizedUserDetails);

        assertThat(result).isNotNull();
        assertThat(result.getMenuId()).isEqualTo(menuId);
    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = {"CUSTOMER", "RIDER"})
    @DisplayName("메뉴 수정 권한이 없는 역할 테스트 (CUSTOMER, RIDER)")
    void updateMenu_UnauthorizedRoles_ThrowsException(Role role) {
        User unauthorizedUser = Factory.createMockUserWithRole(role);
        CustomUserDetails unauthorizedUserDetails = createCustomUserDetails(unauthorizedUser);

        UUID menuId = UUID.randomUUID();
        Store testStore = mock(Store.class);
        Menu testMenu = mock(Menu.class);

        when(testMenu.getStore()).thenReturn(testStore);
        when(testStore.getUser()).thenReturn(testOwnerUser);

        MenuRequestDTO.MenuUpdateRequestDTO requestDTO = MenuRequestDTO.MenuUpdateRequestDTO.builder()
                .name("권한없음수정메뉴_" + role.name())
                .category("치킨류")
                .build();

        given(menuRepository.findById(menuId))
                .willReturn(Optional.of(testMenu));

        assertThatThrownBy(() -> menuCommandService.updateMenu(menuId, requestDTO, unauthorizedUserDetails))
                .isInstanceOf(MenuException.class);
    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = {"OWNER", "MASTER"})
    @DisplayName("메뉴 삭제 권한이 있는 역할 테스트 (OWNER, MASTER)")
    void deleteMenu_AuthorizedRoles_Success(Role role) {
        User authorizedUser = Factory.createMockUserWithRole(role);
        CustomUserDetails authorizedUserDetails = createCustomUserDetails(authorizedUser);

        UUID menuId = UUID.randomUUID();
        Store testStore = mock(Store.class);
        Menu testMenu = mock(Menu.class);

        when(testMenu.getStore()).thenReturn(testStore);
        when(testStore.getUser()).thenReturn(authorizedUser);

        given(menuRepository.findById(menuId))
                .willReturn(Optional.of(testMenu));

        assertThatCode(() -> menuCommandService.deleteMenu(menuId, authorizedUserDetails))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = {"CUSTOMER", "RIDER"})
    @DisplayName("메뉴 삭제 권한이 없는 역할 테스트 (CUSTOMER, RIDER)")
    void deleteMenu_UnauthorizedRoles_ThrowsException(Role role) {
        User unauthorizedUser = Factory.createMockUserWithRole(role);
        CustomUserDetails unauthorizedUserDetails = createCustomUserDetails(unauthorizedUser);

        UUID menuId = UUID.randomUUID();
        Store testStore = mock(Store.class);
        Menu testMenu = mock(Menu.class);

        when(testMenu.getStore()).thenReturn(testStore);
        when(testStore.getUser()).thenReturn(testOwnerUser);

        given(menuRepository.findById(menuId))
                .willReturn(Optional.of(testMenu));

        assertThatThrownBy(() -> menuCommandService.deleteMenu(menuId, unauthorizedUserDetails))
                .isInstanceOf(MenuException.class);
    }

    @Test
    @DisplayName("가게를 찾을 수 없는 경우 예외 발생")
    void createMenu_StoreNotFound_ThrowsException() {
        UUID nonExistentStoreId = UUID.randomUUID();
        MenuRequestDTO.MenuCreateRequestDTO requestDTO = MenuRequestDTO.MenuCreateRequestDTO.builder()
                .name("신메뉴 BBQ치킨")
                .content("새로운 BBQ 소스로 맛을 낸 치킨")
                .price(22000)
                .category("치킨류")
                .build();

        given(storeRepository.findByIdAndIsDeletedFalse(nonExistentStoreId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> menuCommandService.createMenu(requestDTO, nonExistentStoreId, ownerUserDetails))
                .isInstanceOf(StoreException.class);
    }

    @Test
    @DisplayName("존재하지 않는 메뉴 카테고리로 메뉴 생성 시 새 카테고리 자동 생성")
    void createMenu_NewCategoryCreated_Success() {
        UUID storeId = UUID.randomUUID();
        Store testStore = mock(Store.class);
        when(testStore.getId()).thenReturn(storeId);
        when(testStore.getName()).thenReturn("맛있는치킨");

        MenuRequestDTO.MenuCreateRequestDTO requestDTO = MenuRequestDTO.MenuCreateRequestDTO.builder()
                .name("신메뉴")
                .content("설명")
                .price(10000)
                .category("새로운카테고리")
                .status(MenuStatus.판매중)
                .build();

        MenuCategory newMenuCategory = mock(MenuCategory.class);
        when(newMenuCategory.getId()).thenReturn(UUID.randomUUID());
        when(newMenuCategory.getCategory()).thenReturn("새로운카테고리");

        Menu newMenu = mock(Menu.class);
        when(newMenu.getId()).thenReturn(UUID.randomUUID());
        when(newMenu.getName()).thenReturn("신메뉴");
        when(newMenu.getPrice()).thenReturn(10000);
        when(newMenu.getContent()).thenReturn("설명");
        when(newMenu.getStatus()).thenReturn(MenuStatus.판매중);
        when(newMenu.getStore()).thenReturn(testStore);
        when(newMenu.getMenuCategory()).thenReturn(newMenuCategory);
        when(newMenu.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(newMenu.getUpdatedAt()).thenReturn(LocalDateTime.now());

        given(storeRepository.findByIdAndIsDeletedFalse(storeId))
                .willReturn(Optional.of(testStore));
        given(menuCategoryRepository.findByCategory("새로운카테고리"))
                .willReturn(Optional.empty());
        given(menuCategoryRepository.save(any(MenuCategory.class)))
                .willReturn(newMenuCategory);
        given(menuRepository.existsByNameAndStoreId("신메뉴", storeId))
                .willReturn(false);
        given(menuRepository.save(any(Menu.class)))
                .willReturn(newMenu);

        MenuResponseDTO.MenuDetailResponseDTO result = menuCommandService.createMenu(requestDTO, storeId, ownerUserDetails);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("신메뉴");
        assertThat(result.getCategory()).isEqualTo("새로운카테고리");
    }

    @Test
    @DisplayName("중복 메뉴명인 경우 예외 발생")
    void createMenu_DuplicateMenuName_ThrowsException() {
        UUID storeId = UUID.randomUUID();
        Store testStore = mock(Store.class);
        MenuCategory testMenuCategory = mock(MenuCategory.class);

        when(testStore.getId()).thenReturn(storeId);

        MenuRequestDTO.MenuCreateRequestDTO requestDTO = MenuRequestDTO.MenuCreateRequestDTO.builder()
                .name("후라이드치킨")
                .content("이름만 같은 다른 후라이드치킨")
                .price(19000)
                .category("치킨류")
                .build();

        given(storeRepository.findByIdAndIsDeletedFalse(storeId))
                .willReturn(Optional.of(testStore));
        given(menuCategoryRepository.findByCategory("치킨류"))
                .willReturn(Optional.of(testMenuCategory));
        given(menuRepository.existsByNameAndStoreId("후라이드치킨", storeId))
                .willReturn(true);

        assertThatThrownBy(() -> menuCommandService.createMenu(requestDTO, storeId, ownerUserDetails))
                .isInstanceOf(MenuException.class);
    }

    @Test
    @DisplayName("메뉴를 찾을 수 없는 경우 예외 발생")
    void updateMenu_MenuNotFound_ThrowsException() {
        UUID nonExistentMenuId = UUID.randomUUID();
        MenuRequestDTO.MenuUpdateRequestDTO requestDTO = MenuRequestDTO.MenuUpdateRequestDTO.builder()
                .name("존재하지 않는 메뉴")
                .category("치킨류")
                .build();

        given(menuRepository.findById(nonExistentMenuId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> menuCommandService.updateMenu(nonExistentMenuId, requestDTO, ownerUserDetails))
                .isInstanceOf(MenuException.class);
    }

    @Test
    @DisplayName("메뉴 삭제 - 메뉴를 찾을 수 없는 경우 예외 발생")
    void deleteMenu_MenuNotFound_ThrowsException() {
        UUID nonExistentMenuId = UUID.randomUUID();

        given(menuRepository.findById(nonExistentMenuId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> menuCommandService.deleteMenu(nonExistentMenuId, ownerUserDetails))
                .isInstanceOf(MenuException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"CREATE", "UPDATE", "DELETE"})
    @DisplayName("가게 혹은 메뉴 엔티티를 찾을 수 없는 경우 예외 발생 통합 테스트")
    void entityNotFound_ThrowsException(String operation) {
        UUID nonExistentId = UUID.randomUUID();

        switch (operation) {
            case "CREATE" -> {
                MenuRequestDTO.MenuCreateRequestDTO createRequestDTO = MenuRequestDTO.MenuCreateRequestDTO.builder()
                        .name("테스트메뉴")
                        .content("설명")
                        .price(10000)
                        .category("카테고리")
                        .build();

                given(storeRepository.findByIdAndIsDeletedFalse(nonExistentId))
                        .willReturn(Optional.empty());

                assertThatThrownBy(() -> menuCommandService.createMenu(createRequestDTO, nonExistentId, ownerUserDetails))
                        .isInstanceOf(StoreException.class);
            }
            case "UPDATE" -> {
                MenuRequestDTO.MenuUpdateRequestDTO updateRequestDTO = MenuRequestDTO.MenuUpdateRequestDTO.builder()
                        .name("수정메뉴")
                        .category("카테고리")
                        .build();

                given(menuRepository.findById(nonExistentId))
                        .willReturn(Optional.empty());

                assertThatThrownBy(() -> menuCommandService.updateMenu(nonExistentId, updateRequestDTO, ownerUserDetails))
                        .isInstanceOf(MenuException.class);
            }
            case "DELETE" -> {
                given(menuRepository.findById(nonExistentId))
                        .willReturn(Optional.empty());

                assertThatThrownBy(() -> menuCommandService.deleteMenu(nonExistentId, ownerUserDetails))
                        .isInstanceOf(MenuException.class);
            }
        }
    }

    @Test
    @DisplayName("메뉴 옵션 생성 성공 - MASTER 권한")
    void createMenuOption_Master_Success() {
        User masterUser = Factory.createMockUserWithRole(Role.MASTER);
        CustomUserDetails masterUserDetails = createCustomUserDetails(masterUser);

        UUID menuId = UUID.randomUUID();
        Store testStore = mock(Store.class);
        Menu testMenu = mock(Menu.class);

        when(testMenu.getId()).thenReturn(menuId);
        when(testMenu.getName()).thenReturn("후라이드치킨");
        when(testMenu.getStore()).thenReturn(testStore);
        when(testStore.getName()).thenReturn("맛있는치킨집");

        MenuRequestDTO.MenuOptionStandaloneCreateRequestDTO requestDTO =
                MenuRequestDTO.MenuOptionStandaloneCreateRequestDTO.builder()
                        .menuId(menuId)
                        .optionName("매운맛")
                        .additionalPrice(0)
                        .build();

        given(menuRepository.findById(menuId))
                .willReturn(Optional.of(testMenu));
        given(menuOptionRepository.existsByMenuIdAndOptionName(menuId, "매운맛"))
                .willReturn(false);
        given(menuOptionRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        assertThatCode(() -> menuCommandService.createMenuOption(requestDTO, masterUserDetails))
                .doesNotThrowAnyException();

        verify(menuRepository, times(1)).findById(menuId);
        verify(menuOptionRepository, times(1)).existsByMenuIdAndOptionName(menuId, "매운맛");
        verify(menuOptionRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("메뉴 옵션 생성 성공 - OWNER 권한 (본인 가게)")
    void createMenuOption_Owner_Success() {
        UUID menuId = UUID.randomUUID();
        Store testStore = mock(Store.class);
        Menu testMenu = mock(Menu.class);

        when(testMenu.getId()).thenReturn(menuId);
        when(testMenu.getName()).thenReturn("후라이드치킨");
        when(testMenu.getStore()).thenReturn(testStore);
        when(testStore.getUser()).thenReturn(testOwnerUser);
        when(testStore.getName()).thenReturn("맛있는치킨집");

        MenuRequestDTO.MenuOptionStandaloneCreateRequestDTO requestDTO =
                MenuRequestDTO.MenuOptionStandaloneCreateRequestDTO.builder()
                        .menuId(menuId)
                        .optionName("순살로 변경")
                        .additionalPrice(2000)
                        .build();

        given(menuRepository.findById(menuId))
                .willReturn(Optional.of(testMenu));
        given(menuOptionRepository.existsByMenuIdAndOptionName(menuId, "순살로 변경"))
                .willReturn(false);
        given(menuOptionRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        assertThatCode(() -> menuCommandService.createMenuOption(requestDTO, ownerUserDetails))
                .doesNotThrowAnyException();

        verify(menuRepository, times(1)).findById(menuId);
        verify(menuOptionRepository, times(1)).existsByMenuIdAndOptionName(menuId, "순살로 변경");
        verify(menuOptionRepository, times(1)).save(any());
    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = {"CUSTOMER", "RIDER"})
    @DisplayName("메뉴 옵션 생성 권한이 없는 역할 테스트 (CUSTOMER, RIDER)")
    void createMenuOption_UnauthorizedRoles_ThrowsException(Role role) {
        User unauthorizedUser = Factory.createMockUserWithRole(role);
        CustomUserDetails unauthorizedUserDetails = createCustomUserDetails(unauthorizedUser);

        UUID menuId = UUID.randomUUID();
        Menu testMenu = mock(Menu.class);

        MenuRequestDTO.MenuOptionStandaloneCreateRequestDTO requestDTO =
                MenuRequestDTO.MenuOptionStandaloneCreateRequestDTO.builder()
                        .menuId(menuId)
                        .optionName("권한없음옵션")
                        .additionalPrice(1000)
                        .build();

        given(menuRepository.findById(menuId))
                .willReturn(Optional.of(testMenu));

        assertThatThrownBy(() -> menuCommandService.createMenuOption(requestDTO, unauthorizedUserDetails))
                .isInstanceOf(MenuException.class);

        verify(menuRepository, times(1)).findById(menuId);
    }

    @Test
    @DisplayName("메뉴 옵션 생성 - OWNER가 다른 가게 메뉴에 옵션 생성 시도 시 예외 발생")
    void createMenuOption_OwnerUnauthorizedStore_ThrowsException() {
        User otherOwner = Factory.createMockUserWithRole(Role.OWNER);
        UUID menuId = UUID.randomUUID();
        Store otherStore = mock(Store.class);
        Menu testMenu = mock(Menu.class);

        when(testMenu.getStore()).thenReturn(otherStore);
        when(otherStore.getUser()).thenReturn(otherOwner);

        MenuRequestDTO.MenuOptionStandaloneCreateRequestDTO requestDTO =
                MenuRequestDTO.MenuOptionStandaloneCreateRequestDTO.builder()
                        .menuId(menuId)
                        .optionName("권한없음옵션")
                        .additionalPrice(1000)
                        .build();

        given(menuRepository.findById(menuId))
                .willReturn(Optional.of(testMenu));

        assertThatThrownBy(() -> menuCommandService.createMenuOption(requestDTO, ownerUserDetails))
                .isInstanceOf(MenuException.class);

        verify(menuRepository, times(1)).findById(menuId);
    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = {"OWNER", "MASTER"})
    @DisplayName("메뉴 옵션 수정 권한이 있는 역할 테스트 (OWNER, MASTER)")
    void updateMenuOption_AuthorizedRoles_Success(Role role) {
        User authorizedUser = Factory.createMockUserWithRole(role);
        CustomUserDetails authorizedUserDetails = createCustomUserDetails(authorizedUser);

        UUID optionId = UUID.randomUUID();
        UUID menuId = UUID.randomUUID();
        Store testStore = mock(Store.class);
        Menu testMenu = mock(Menu.class);
        com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuOption testMenuOption =
            mock(com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuOption.class);

        when(testMenuOption.getId()).thenReturn(optionId);
        when(testMenuOption.getOptionName()).thenReturn("기존옵션_" + role.name());
        when(testMenuOption.getAdditionalPrice()).thenReturn(500);
        when(testMenuOption.getMenu()).thenReturn(testMenu);
        when(testMenu.getId()).thenReturn(menuId);
        when(testMenu.getName()).thenReturn("후라이드치킨");
        when(testMenu.getStore()).thenReturn(testStore);
        when(testStore.getName()).thenReturn("맛있는치킨집");

        if (role == Role.OWNER) {
            when(testStore.getUser()).thenReturn(authorizedUser);
        }

        MenuRequestDTO.MenuOptionStandaloneUpdateRequestDTO requestDTO =
                MenuRequestDTO.MenuOptionStandaloneUpdateRequestDTO.builder()
                        .optionName("수정된옵션_" + role.name())
                        .additionalPrice(role == Role.MASTER ? 1500 : 2000)
                        .build();

        given(menuOptionRepository.findByIdWithMenu(optionId))
                .willReturn(Optional.of(testMenuOption));
        given(menuOptionRepository.existsByMenuIdAndOptionName(menuId, "수정된옵션_" + role.name()))
                .willReturn(false);
        given(menuOptionRepository.save(testMenuOption))
                .willReturn(testMenuOption);

        assertThatCode(() -> menuCommandService.updateMenuOption(optionId, requestDTO, authorizedUserDetails))
                .doesNotThrowAnyException();

        verify(menuOptionRepository, times(1)).findByIdWithMenu(optionId);
        verify(menuOptionRepository, times(1)).existsByMenuIdAndOptionName(menuId, "수정된옵션_" + role.name());
        verify(testMenuOption, times(1)).updateOptionInfo("수정된옵션_" + role.name(), role == Role.MASTER ? 1500 : 2000);
        verify(menuOptionRepository, times(1)).save(testMenuOption);
    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = {"OWNER", "MASTER"})
    @DisplayName("메뉴 옵션 삭제 권한이 있는 역할 테스트 (OWNER, MASTER)")
    void deleteMenuOption_AuthorizedRoles_Success(Role role) {
        User authorizedUser = Factory.createMockUserWithRole(role);
        CustomUserDetails authorizedUserDetails = createCustomUserDetails(authorizedUser);

        UUID optionId = UUID.randomUUID();
        Store testStore = mock(Store.class);
        Menu testMenu = mock(Menu.class);
        com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuOption testMenuOption =
            mock(com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuOption.class);

        when(testMenuOption.getMenu()).thenReturn(testMenu);
        when(testMenu.getStore()).thenReturn(testStore);

        // OWNER인 경우에만 가게 소유자 검증을 위한 설정
        if (role == Role.OWNER) {
            when(testStore.getUser()).thenReturn(authorizedUser);
        }

        given(menuOptionRepository.findByIdWithMenu(optionId))
                .willReturn(Optional.of(testMenuOption));

        assertThatCode(() -> menuCommandService.deleteMenuOption(optionId, authorizedUserDetails))
                .doesNotThrowAnyException();

        verify(menuOptionRepository, times(1)).findByIdWithMenu(optionId);
        verify(menuOptionRepository, times(1)).delete(testMenuOption);
    }


    @Test
    @DisplayName("메뉴 옵션 수정 - OWNER가 다른 가게 메뉴 옵션 수정 시도 시 예외 발생")
    void updateMenuOption_OwnerUnauthorizedStore_ThrowsException() {
        User otherOwner = Factory.createMockUserWithRole(Role.OWNER);
        UUID optionId = UUID.randomUUID();
        UUID menuId = UUID.randomUUID();
        Store otherStore = mock(Store.class);
        Menu testMenu = mock(Menu.class);
        com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuOption testMenuOption =
            mock(com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuOption.class);

        when(testMenuOption.getMenu()).thenReturn(testMenu);
        when(testMenu.getStore()).thenReturn(otherStore);
        when(otherStore.getUser()).thenReturn(otherOwner);

        MenuRequestDTO.MenuOptionStandaloneUpdateRequestDTO requestDTO =
                MenuRequestDTO.MenuOptionStandaloneUpdateRequestDTO.builder()
                        .optionName("권한없음수정")
                        .additionalPrice(1000)
                        .build();

        given(menuOptionRepository.findByIdWithMenu(optionId))
                .willReturn(Optional.of(testMenuOption));

        assertThatThrownBy(() -> menuCommandService.updateMenuOption(optionId, requestDTO, ownerUserDetails))
                .isInstanceOf(MenuException.class);

        verify(menuOptionRepository, times(1)).findByIdWithMenu(optionId);
    }

    @Test
    @DisplayName("메뉴 옵션 삭제 - OWNER가 다른 가게 메뉴 옵션 삭제 시도 시 예외 발생")
    void deleteMenuOption_OwnerUnauthorizedStore_ThrowsException() {
        User otherOwner = Factory.createMockUserWithRole(Role.OWNER);
        UUID optionId = UUID.randomUUID();
        Store otherStore = mock(Store.class);
        Menu testMenu = mock(Menu.class);
        com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuOption testMenuOption =
            mock(com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuOption.class);

        when(testMenuOption.getMenu()).thenReturn(testMenu);
        when(testMenu.getStore()).thenReturn(otherStore);
        when(otherStore.getUser()).thenReturn(otherOwner);

        given(menuOptionRepository.findByIdWithMenu(optionId))
                .willReturn(Optional.of(testMenuOption));

        assertThatThrownBy(() -> menuCommandService.deleteMenuOption(optionId, ownerUserDetails))
                .isInstanceOf(MenuException.class);

        verify(menuOptionRepository, times(1)).findByIdWithMenu(optionId);
    }

    @Test
    @DisplayName("메뉴 옵션 생성 - 중복 옵션명 시 예외 발생")
    void createMenuOption_DuplicateOptionName_ThrowsException() {
        UUID menuId = UUID.randomUUID();
        Store testStore = mock(Store.class);
        Menu testMenu = mock(Menu.class);

        when(testMenu.getId()).thenReturn(menuId);
        when(testMenu.getStore()).thenReturn(testStore);
        when(testStore.getUser()).thenReturn(testOwnerUser);

        MenuRequestDTO.MenuOptionStandaloneCreateRequestDTO requestDTO =
                MenuRequestDTO.MenuOptionStandaloneCreateRequestDTO.builder()
                        .menuId(menuId)
                        .optionName("곱배기")
                        .additionalPrice(1000)
                        .build();

        given(menuRepository.findById(menuId))
                .willReturn(Optional.of(testMenu));
        given(menuOptionRepository.existsByMenuIdAndOptionName(menuId, "곱배기"))
                .willReturn(true);

        assertThatThrownBy(() -> menuCommandService.createMenuOption(requestDTO, ownerUserDetails))
                .isInstanceOf(MenuException.class);

        verify(menuRepository, times(1)).findById(menuId);
        verify(menuOptionRepository, times(1)).existsByMenuIdAndOptionName(menuId, "곱배기");
    }

    @Test
    @DisplayName("메뉴 옵션 수정 - 중복 옵션명 시 예외 발생")
    void updateMenuOption_DuplicateOptionName_ThrowsException() {
        UUID optionId = UUID.randomUUID();
        UUID menuId = UUID.randomUUID();
        Store testStore = mock(Store.class);
        Menu testMenu = mock(Menu.class);
        com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuOption testMenuOption =
            mock(com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuOption.class);

        when(testMenuOption.getId()).thenReturn(optionId);
        when(testMenuOption.getOptionName()).thenReturn("기존옵션");
        when(testMenuOption.getMenu()).thenReturn(testMenu);
        when(testMenu.getId()).thenReturn(menuId);
        when(testMenu.getStore()).thenReturn(testStore);
        when(testStore.getUser()).thenReturn(testOwnerUser);

        MenuRequestDTO.MenuOptionStandaloneUpdateRequestDTO requestDTO =
                MenuRequestDTO.MenuOptionStandaloneUpdateRequestDTO.builder()
                        .optionName("중복옵션")
                        .additionalPrice(1000)
                        .build();

        given(menuOptionRepository.findByIdWithMenu(optionId))
                .willReturn(Optional.of(testMenuOption));
        given(menuOptionRepository.existsByMenuIdAndOptionName(menuId, "중복옵션"))
                .willReturn(true);

        assertThatThrownBy(() -> menuCommandService.updateMenuOption(optionId, requestDTO, ownerUserDetails))
                .isInstanceOf(MenuException.class);

        verify(menuOptionRepository, times(1)).findByIdWithMenu(optionId);
        verify(menuOptionRepository, times(1)).existsByMenuIdAndOptionName(menuId, "중복옵션");
    }

    @Test
    @DisplayName("메뉴 옵션 수정 - 존재하지 않는 옵션 시 예외 발생")
    void updateMenuOption_OptionNotFound_ThrowsException() {
        UUID nonExistentOptionId = UUID.randomUUID();

        MenuRequestDTO.MenuOptionStandaloneUpdateRequestDTO requestDTO =
                MenuRequestDTO.MenuOptionStandaloneUpdateRequestDTO.builder()
                        .optionName("존재하지않는옵션")
                        .additionalPrice(1000)
                        .build();

        given(menuOptionRepository.findByIdWithMenu(nonExistentOptionId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> menuCommandService.updateMenuOption(nonExistentOptionId, requestDTO, ownerUserDetails))
                .isInstanceOf(MenuException.class);

        verify(menuOptionRepository, times(1)).findByIdWithMenu(nonExistentOptionId);
    }

    @Test
    @DisplayName("메뉴 옵션 삭제 - 존재하지 않는 옵션 시 예외 발생")
    void deleteMenuOption_OptionNotFound_ThrowsException() {
        UUID nonExistentOptionId = UUID.randomUUID();

        given(menuOptionRepository.findByIdWithMenu(nonExistentOptionId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> menuCommandService.deleteMenuOption(nonExistentOptionId, ownerUserDetails))
                .isInstanceOf(MenuException.class);

        verify(menuOptionRepository, times(1)).findByIdWithMenu(nonExistentOptionId);
    }

    @Test
    @DisplayName("메뉴 옵션 수정 - 이름은 같지만 다른 옵션명으로 중복 체크 통과")
    void updateMenuOption_SameNameNoConflict_Success() {
        UUID optionId = UUID.randomUUID();
        UUID menuId = UUID.randomUUID();
        Store testStore = mock(Store.class);
        Menu testMenu = mock(Menu.class);
        com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuOption testMenuOption =
            mock(com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuOption.class);

        when(testMenuOption.getId()).thenReturn(optionId);
        when(testMenuOption.getOptionName()).thenReturn("기존옵션");
        when(testMenuOption.getMenu()).thenReturn(testMenu);
        when(testMenu.getId()).thenReturn(menuId);
        when(testMenu.getName()).thenReturn("후라이드치킨");
        when(testMenu.getStore()).thenReturn(testStore);
        when(testStore.getUser()).thenReturn(testOwnerUser);
        when(testStore.getName()).thenReturn("맛있는치킨집");

        MenuRequestDTO.MenuOptionStandaloneUpdateRequestDTO requestDTO =
                MenuRequestDTO.MenuOptionStandaloneUpdateRequestDTO.builder()
                        .optionName("기존옵션")
                        .additionalPrice(2000)
                        .build();

        given(menuOptionRepository.findByIdWithMenu(optionId))
                .willReturn(Optional.of(testMenuOption));
        given(menuOptionRepository.save(testMenuOption))
                .willReturn(testMenuOption);

        assertThatCode(() -> menuCommandService.updateMenuOption(optionId, requestDTO, ownerUserDetails))
                .doesNotThrowAnyException();

        verify(menuOptionRepository, times(1)).findByIdWithMenu(optionId);
        verify(menuOptionRepository, never()).existsByMenuIdAndOptionName(any(), any());
        verify(testMenuOption, times(1)).updateOptionInfo("기존옵션", 2000);
    }
}
