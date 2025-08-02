package com.example.cloudfour.peopleofdelivery.unit.domain.menu.service.query;

import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.Menu;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuCategory;
import com.example.cloudfour.peopleofdelivery.domain.menu.enums.MenuStatus;
import com.example.cloudfour.peopleofdelivery.domain.menu.exception.MenuException;
import com.example.cloudfour.peopleofdelivery.domain.menu.repository.MenuCategoryRepository;
import com.example.cloudfour.peopleofdelivery.domain.menu.repository.MenuRepository;
import com.example.cloudfour.peopleofdelivery.domain.menu.service.query.MenuQueryServiceImpl;
import com.example.cloudfour.peopleofdelivery.domain.region.entity.Region;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.Role;
import com.example.cloudfour.peopleofdelivery.domain.user.repository.UserRepository;
import com.example.cloudfour.peopleofdelivery.fixtures.Factory;
import com.example.cloudfour.peopleofdelivery.global.auth.userdetails.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("메뉴 Query 서비스 테스트")
class MenuQueryServiceImplTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuCategoryRepository menuCategoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private MenuQueryServiceImpl menuQueryService;

    private User testOwnerUser;
    private Store testStore;
    private MenuCategory testMenuCategory;
    private Menu testMenu;
    private Region testRegion;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        testOwnerUser = Factory.createMockUserWithAll();
        testStore = testOwnerUser.getStores().getFirst();
        testMenu = testStore.getMenus().getFirst();
        testMenuCategory = testMenu.getMenuCategory();
        testRegion = testStore.getRegion();

        userDetails = createCustomUserDetails(testOwnerUser);
    }

    private CustomUserDetails createCustomUserDetails(User user) {
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        lenient().when(userDetails.getId()).thenReturn(user.getId());
        lenient().when(userDetails.getRole()).thenReturn(user.getRole());
        return userDetails;
    }

    @Test
    @DisplayName("특정 가게의 메뉴 목록 조회 성공")
    void getMenusByStore_Success() {
        UUID storeId = testStore.getId();
        LocalDateTime cursor = LocalDateTime.now().plusDays(1);
        Integer size = 10;

        List<Menu> mockMenus = Arrays.asList(
                createMockMenuWithFactory("황금올리브치킨", 18000),
                createMockMenuWithFactory("마르게리타피자", 25000),
                createMockMenuWithFactory("불고기버거", 12000)
        );

        Slice<Menu> mockSlice = mock(Slice.class);
        when(mockSlice.getContent()).thenReturn(mockMenus);
        when(mockSlice.hasNext()).thenReturn(false);
        when(mockSlice.isEmpty()).thenReturn(false);

        given(userRepository.findById(userDetails.getId()))
                .willReturn(Optional.of(testOwnerUser));
        given(storeRepository.findByIdAndIsDeletedFalse(storeId))
                .willReturn(Optional.of(testStore));
        given(menuRepository.findByStoreIdAndDeletedFalseAndCreatedAtBefore(eq(storeId), eq(cursor), any(Pageable.class)))
                .willReturn(mockSlice);

        MenuResponseDTO.MenuStoreListResponseDTO result = menuQueryService.getMenusByStoreWithCursor(storeId, cursor, size, userDetails);

        assertThat(result).isNotNull();
        assertThat(result.getMenus()).hasSize(3);
        assertThat(result.getHasNext()).isFalse();

        assertThat(result.getMenus())
                .extracting(MenuResponseDTO.MenuListResponseDTO::getName)
                .containsExactly("황금올리브치킨", "마르게리타피자", "불고기버거");

        assertThat(result.getMenus())
                .extracting(MenuResponseDTO.MenuListResponseDTO::getPrice)
                .containsExactly(18000, 25000, 12000);

        verify(userRepository, times(1)).findById(userDetails.getId());
        verify(storeRepository, times(1)).findByIdAndIsDeletedFalse(storeId);
        verify(menuRepository, times(1)).findByStoreIdAndDeletedFalseAndCreatedAtBefore(eq(storeId), eq(cursor), any(Pageable.class));
    }

    @Test
    @DisplayName("카테고리별 메뉴 목록 조회 성공")
    void getMenusByStoreWithCategory_Success() {
        UUID storeId = testStore.getId();
        UUID categoryId = testMenuCategory.getId();
        LocalDateTime cursor = LocalDateTime.now().plusDays(1);
        Integer size = 10;

        List<Menu> mockMenus = Arrays.asList(
                createMockMenuWithFactory("후라이드치킨", 18000),
                createMockMenuWithFactory("양념치킨", 19000)
        );

        Slice<Menu> mockSlice = mock(Slice.class);
        when(mockSlice.getContent()).thenReturn(mockMenus);
        when(mockSlice.hasNext()).thenReturn(false);
        when(mockSlice.isEmpty()).thenReturn(false);

        given(userRepository.findById(userDetails.getId()))
                .willReturn(Optional.of(testOwnerUser));
        given(storeRepository.findByIdAndIsDeletedFalse(storeId))
                .willReturn(Optional.of(testStore));
        given(menuCategoryRepository.findById(categoryId))
                .willReturn(Optional.of(testMenuCategory));
        given(menuRepository.findByStoreIdAndMenuCategoryIdAndDeletedFalseAndCreatedAtBefore(eq(storeId), eq(categoryId), eq(cursor), any(Pageable.class)))
                .willReturn(mockSlice);

        MenuResponseDTO.MenuStoreListResponseDTO result = menuQueryService.getMenusByStoreWithCategory(storeId, categoryId, cursor, size, userDetails);

        assertThat(result).isNotNull();
        assertThat(result.getMenus()).hasSize(2);
        assertThat(result.getHasNext()).isFalse();

        verify(userRepository, times(1)).findById(userDetails.getId());
        verify(storeRepository, times(1)).findByIdAndIsDeletedFalse(storeId);
        verify(menuCategoryRepository, times(1)).findById(categoryId);
        verify(menuRepository, times(1)).findByStoreIdAndMenuCategoryIdAndDeletedFalseAndCreatedAtBefore(eq(storeId), eq(categoryId), eq(cursor), any(Pageable.class));
    }

    @Test
    @DisplayName("빈 가게의 메뉴 목록 조회 시 예외 발생")
    void getMenusByStore_EmptyStore_ThrowsException() {
        UUID emptyStoreId = UUID.randomUUID();
        LocalDateTime cursor = LocalDateTime.now().plusDays(1);
        Integer size = 10;

        Slice<Menu> emptySlice = mock(Slice.class);
        when(emptySlice.getContent()).thenReturn(Arrays.asList());
        when(emptySlice.hasNext()).thenReturn(false);
        when(emptySlice.isEmpty()).thenReturn(true);

        given(userRepository.findById(userDetails.getId()))
                .willReturn(Optional.of(testOwnerUser));
        given(storeRepository.findByIdAndIsDeletedFalse(emptyStoreId))
                .willReturn(Optional.of(testStore));
        given(menuRepository.findByStoreIdAndDeletedFalseAndCreatedAtBefore(eq(emptyStoreId), eq(cursor), any(Pageable.class)))
                .willReturn(emptySlice);

        assertThatThrownBy(() -> menuQueryService.getMenusByStoreWithCursor(emptyStoreId, cursor, size, userDetails))
                .isInstanceOf(MenuException.class);

        verify(userRepository, times(1)).findById(userDetails.getId());
        verify(storeRepository, times(1)).findByIdAndIsDeletedFalse(emptyStoreId);
        verify(menuRepository, times(1)).findByStoreIdAndDeletedFalseAndCreatedAtBefore(eq(emptyStoreId), eq(cursor), any(Pageable.class));
    }

    @Test
    @DisplayName("인기 메뉴 TOP20 조회 성공")
    void getTopMenus_Success() {
        List<Menu> mockTopMenus = IntStream.range(1, 21)
                .mapToObj(i -> createMockMenuWithFactory(i + "위 인기메뉴", 15000 + i * 1000))
                .collect(Collectors.toList());

        given(userRepository.findById(userDetails.getId()))
                .willReturn(Optional.of(testOwnerUser));
        given(menuRepository.findTopMenusByOrderCount(PageRequest.of(0, 20)))
                .willReturn(mockTopMenus);

        List<MenuResponseDTO.MenuTopResponseDTO> result = menuQueryService.getTopMenus(userDetails);

        assertThat(result).hasSize(20);
        assertThat(result.get(0).getName()).isEqualTo("1위 인기메뉴");
        assertThat(result.get(0).getPrice()).isEqualTo(16000);
        assertThat(result.get(19).getName()).isEqualTo("20위 인기메뉴");
        assertThat(result.get(19).getPrice()).isEqualTo(35000);

        verify(userRepository, times(1)).findById(userDetails.getId());
        verify(menuRepository, times(1)).findTopMenusByOrderCount(PageRequest.of(0, 20));
    }

    @Test
    @DisplayName("시간대별 인기 메뉴 TOP20 조회 성공")
    void getTimeTopMenus_Success() {
        List<Menu> mockTimeTopMenus = IntStream.range(1, 21)
                .mapToObj(i -> createMockMenuWithFactory("시간대별 " + i + "위 메뉴", 12000 + i * 500))
                .collect(Collectors.toList());

        given(userRepository.findById(userDetails.getId()))
                .willReturn(Optional.of(testOwnerUser));
        given(menuRepository.findTopMenusByTimeRange(any(), any(), any()))
                .willReturn(mockTimeTopMenus);

        List<MenuResponseDTO.MenuTimeTopResponseDTO> result = menuQueryService.getTimeTopMenus(userDetails);

        assertThat(result).hasSize(20);
        assertThat(result.get(0).getName()).isEqualTo("시간대별 1위 메뉴");
        assertThat(result.get(0).getPrice()).isEqualTo(12500);

        verify(userRepository, times(1)).findById(userDetails.getId());
        verify(menuRepository, times(1)).findTopMenusByTimeRange(any(), any(), any());
    }

    @Test
    @DisplayName("지역별 인기 메뉴 TOP20 조회 성공")
    void getRegionTopMenus_Success() {
        String si = "서울특별시";
        String gu = "강남구";
        List<Menu> mockRegionTopMenus = IntStream.range(1, 21)
                .mapToObj(i -> createMockMenuWithFactory("지역별 " + i + "위 메뉴", 10000 + i * 800))
                .collect(Collectors.toList());

        given(userRepository.findById(userDetails.getId()))
                .willReturn(Optional.of(testOwnerUser));
        given(menuRepository.findTopMenusByRegion(eq(si), eq(gu), any()))
                .willReturn(mockRegionTopMenus);

        List<MenuResponseDTO.MenuRegionTopResponseDTO> result = menuQueryService.getRegionTopMenus(si, gu, userDetails);

        assertThat(result).hasSize(20);
        assertThat(result.get(0).getName()).isEqualTo("지역별 1위 메뉴");
        assertThat(result.get(0).getPrice()).isEqualTo(10800);

        verify(userRepository, times(1)).findById(userDetails.getId());
        verify(menuRepository, times(1)).findTopMenusByRegion(eq(si), eq(gu), any());
    }

    @Test
    @DisplayName("메뉴 상세 조회 성공")
    void getMenuDetail_Success() {
        UUID menuId = testMenu.getId();

        given(userRepository.findById(userDetails.getId()))
                .willReturn(Optional.of(testOwnerUser));
        given(menuRepository.findById(menuId))
                .willReturn(Optional.of(testMenu));

        MenuResponseDTO.MenuDetailResponseDTO result = menuQueryService.getMenuDetail(menuId, userDetails);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("후라이드치킨");
        assertThat(result.getPrice()).isEqualTo(18000);
        assertThat(result.getContent()).isEqualTo("기본 치킨");
        assertThat(result.getStatus()).isEqualTo(MenuStatus.판매중);

        verify(userRepository, times(1)).findById(userDetails.getId());
        verify(menuRepository, times(1)).findById(menuId);
    }

    @Test
    @DisplayName("존재하지 않는 메뉴 상세 조회 시 예외 발생")
    void getMenuDetail_MenuNotFound_ThrowsException() {
        UUID nonExistentMenuId = UUID.randomUUID();

        given(userRepository.findById(userDetails.getId()))
                .willReturn(Optional.of(testOwnerUser));
        given(menuRepository.findById(nonExistentMenuId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> menuQueryService.getMenuDetail(nonExistentMenuId, userDetails))
                .isInstanceOf(MenuException.class);

        verify(userRepository, times(1)).findById(userDetails.getId());
        verify(menuRepository, times(1)).findById(nonExistentMenuId);
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 메뉴 조회 시 예외 발생")
    void getMenusByStore_UserNotFound_ThrowsException() {
        UUID storeId = testStore.getId();
        LocalDateTime cursor = LocalDateTime.now().plusDays(1);
        Integer size = 10;

        CustomUserDetails invalidUserDetails = mock(CustomUserDetails.class);
        when(invalidUserDetails.getId()).thenReturn(UUID.randomUUID());
        when(invalidUserDetails.getRole()).thenReturn(Role.CUSTOMER);

        given(userRepository.findById(invalidUserDetails.getId()))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> menuQueryService.getMenusByStoreWithCursor(storeId, cursor, size, invalidUserDetails))
                .isInstanceOf(com.example.cloudfour.peopleofdelivery.domain.user.exception.UserException.class);

        verify(userRepository, times(1)).findById(invalidUserDetails.getId());
    }

    @Test
    @DisplayName("존재하지 않는 가게로 메뉴 조회 시 예외 발생")
    void getMenusByStore_StoreNotFound_ThrowsException() {
        UUID nonExistentStoreId = UUID.randomUUID();
        LocalDateTime cursor = LocalDateTime.now().plusDays(1);
        Integer size = 10;

        given(userRepository.findById(userDetails.getId()))
                .willReturn(Optional.of(testOwnerUser));
        given(storeRepository.findByIdAndIsDeletedFalse(nonExistentStoreId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> menuQueryService.getMenusByStoreWithCursor(nonExistentStoreId, cursor, size, userDetails))
                .isInstanceOf(com.example.cloudfour.peopleofdelivery.domain.store.exception.StoreException.class);

        verify(userRepository, times(1)).findById(userDetails.getId());
        verify(storeRepository, times(1)).findByIdAndIsDeletedFalse(nonExistentStoreId);
    }

    @Test
    @DisplayName("cursor가 null일 때 기본값 사용 테스트")
    void getMenusByStore_NullCursor_UsesDefaultCursor() {
        UUID storeId = testStore.getId();
        LocalDateTime cursor = null;
        Integer size = 10;

        List<Menu> mockMenus = Arrays.asList(
                createMockMenuWithFactory("기본커서테스트메뉴", 15000)
        );

        Slice<Menu> mockSlice = mock(Slice.class);
        when(mockSlice.getContent()).thenReturn(mockMenus);
        when(mockSlice.hasNext()).thenReturn(false);
        when(mockSlice.isEmpty()).thenReturn(false);

        given(userRepository.findById(userDetails.getId()))
                .willReturn(Optional.of(testOwnerUser));
        given(storeRepository.findByIdAndIsDeletedFalse(storeId))
                .willReturn(Optional.of(testStore));
        given(menuRepository.findByStoreIdAndDeletedFalseAndCreatedAtBefore(eq(storeId), any(LocalDateTime.class), any(Pageable.class)))
                .willReturn(mockSlice);

        MenuResponseDTO.MenuStoreListResponseDTO result = menuQueryService.getMenusByStoreWithCursor(storeId, cursor, size, userDetails);

        assertThat(result).isNotNull();
        assertThat(result.getMenus()).hasSize(1);

        verify(menuRepository, times(1)).findByStoreIdAndDeletedFalseAndCreatedAtBefore(eq(storeId), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    @DisplayName("존재하지 않는 카테고리로 메뉴 조회 시 예외 발생")
    void getMenusByStoreWithCategory_CategoryNotFound_ThrowsException() {
        UUID storeId = testStore.getId();
        UUID nonExistentCategoryId = UUID.randomUUID();
        LocalDateTime cursor = LocalDateTime.now().plusDays(1);
        Integer size = 10;

        given(userRepository.findById(userDetails.getId()))
                .willReturn(Optional.of(testOwnerUser));
        given(storeRepository.findByIdAndIsDeletedFalse(storeId))
                .willReturn(Optional.of(testStore));
        given(menuCategoryRepository.findById(nonExistentCategoryId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> menuQueryService.getMenusByStoreWithCategory(storeId, nonExistentCategoryId, cursor, size, userDetails))
                .isInstanceOf(com.example.cloudfour.peopleofdelivery.domain.menu.exception.MenuCategoryException.class);

        verify(userRepository, times(1)).findById(userDetails.getId());
        verify(storeRepository, times(1)).findByIdAndIsDeletedFalse(storeId);
        verify(menuCategoryRepository, times(1)).findById(nonExistentCategoryId);
    }

    @Test
    @DisplayName("빈 결과에서 nextCursor가 null인지 확인")
    void getMenusByStore_EmptyResult_NextCursorIsNull() {
        UUID storeId = testStore.getId();
        LocalDateTime cursor = LocalDateTime.now().plusDays(1);
        Integer size = 10;

        Slice<Menu> emptySlice = mock(Slice.class);
        when(emptySlice.getContent()).thenReturn(Arrays.asList());
        when(emptySlice.hasNext()).thenReturn(false);
        when(emptySlice.isEmpty()).thenReturn(true);

        given(userRepository.findById(userDetails.getId()))
                .willReturn(Optional.of(testOwnerUser));
        given(storeRepository.findByIdAndIsDeletedFalse(storeId))
                .willReturn(Optional.of(testStore));
        given(menuRepository.findByStoreIdAndDeletedFalseAndCreatedAtBefore(eq(storeId), eq(cursor), any(Pageable.class)))
                .willReturn(emptySlice);

        assertThatThrownBy(() -> menuQueryService.getMenusByStoreWithCursor(storeId, cursor, size, userDetails))
                .isInstanceOf(MenuException.class);
    }

    @ParameterizedTest
    @EnumSource(Role.class)
    @DisplayName("모든 Role 사용자의 메뉴 조회 성공 - Query는 모든 권한 허용")
    void getMenusByStore_AllRoles_Success(Role role) {
        UUID storeId = testStore.getId();
        LocalDateTime cursor = LocalDateTime.now().plusDays(1);
        Integer size = 10;

        List<Menu> mockMenus = Arrays.asList(
                createMockMenuWithFactory("권한테스트메뉴1_" + role.name(), 15000),
                createMockMenuWithFactory("권한테스트메뉴2_" + role.name(), 16000)
        );

        Slice<Menu> mockSlice = mock(Slice.class);
        when(mockSlice.getContent()).thenReturn(mockMenus);
        when(mockSlice.hasNext()).thenReturn(false);
        when(mockSlice.isEmpty()).thenReturn(false);

        User testUser = Factory.createMockUserWithRole(role);
        CustomUserDetails testUserDetails = createCustomUserDetails(testUser);

        given(userRepository.findById(testUserDetails.getId()))
                .willReturn(Optional.of(testUser));
        given(storeRepository.findByIdAndIsDeletedFalse(storeId))
                .willReturn(Optional.of(testStore));
        given(menuRepository.findByStoreIdAndDeletedFalseAndCreatedAtBefore(eq(storeId), eq(cursor), any(Pageable.class)))
                .willReturn(mockSlice);

        MenuResponseDTO.MenuStoreListResponseDTO result =
                menuQueryService.getMenusByStoreWithCursor(storeId, cursor, size, testUserDetails);

        assertThat(result).isNotNull();
        assertThat(result.getMenus()).hasSize(2);
        assertThat(result.getHasNext()).isFalse();
    }

    private Menu createMockMenuWithFactory(String name, Integer price) {
        Menu menu = Menu.builder()
                .name(name)
                .price(price)
                .content(name + " 설명")
                .status(MenuStatus.판매중)
                .build();

        menu.setStore(testStore);
        menu.setMenuCategory(testMenuCategory);

        setIdReflection(menu, UUID.randomUUID());
        setCreatedAtReflection(menu, LocalDateTime.now().minusHours(1));

        return menu;
    }

    private void setIdReflection(Object entity, UUID id) {
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
        }
    }

    private void setCreatedAtReflection(Object entity, LocalDateTime createdAt) {
        try {
            Field createdAtField = entity.getClass().getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(entity, createdAt);
        } catch (Exception e) {
        }
    }
}
