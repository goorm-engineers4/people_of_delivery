package com.example.cloudfour.peopleofdelivery.unit.domain.menu.service.command;

// 테스트에 필요한 라이브러리들 import
import com.example.cloudfour.peopleofdelivery.mock.TestFixtureFactory;
import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.Menu;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuCategory;
import com.example.cloudfour.peopleofdelivery.domain.menu.enums.MenuStatus;
import com.example.cloudfour.peopleofdelivery.domain.menu.exception.MenuException;
import com.example.cloudfour.peopleofdelivery.domain.menu.repository.MenuCategoryRepository;
import com.example.cloudfour.peopleofdelivery.domain.menu.repository.MenuRepository;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;

// JUnit 5 테스트 관련 import
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

// Mockito (목 객체) 관련 import
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

// AssertJ (검증) 및 Mockito static import
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

// 테스트 클래스 선언
@ExtendWith(MockitoExtension.class)  // Mockito를 JUnit 5와 연동시켜주는 어노테이션
@DisplayName("메뉴 Command 서비스 테스트")  // 테스트 실행할 때 보여질 이름
class MenuCommandServiceImplTest {

    // Mock 객체들 - 실제 객체 대신 가짜 객체 사용
    @Mock  // 가짜 MenuRepository 생성
    private MenuRepository menuRepository;

    @Mock  // 가짜 StoreRepository 생성
    private StoreRepository storeRepository;

    @Mock  // 가짜 MenuCategoryRepository 생성
    private MenuCategoryRepository menuCategoryRepository;

    // 실제 테스트할 대상 - Mock들을 주입받아 생성
    @InjectMocks  // 위의 Mock들을 MenuCommandServiceImpl에 주입
    private MenuCommandServiceImpl menuCommandService;

    // 테스트에서 공통으로 사용할 데이터들
    private User testUserWithAll;      // 테스트용 사용자
    private Store testStore;           // 테스트용 가게
    private MenuCategory testMenuCategory;  // 테스트용 메뉴 카테고리
    private Menu testMenu;             // 테스트용 메뉴

    // 각 테스트 실행 전에 매번 호출되는 메소드
    @BeforeEach  // 모든 @Test 메소드 실행 전마다 호출
    void setUp() {
        // TestFixtureFactory를 활용하여 완전한 테스트 데이터 생성
        testUserWithAll = TestFixtureFactory.createMockUserWithAll();

        // 연관관계를 통해 필요한 객체들 추출
        testStore = testUserWithAll.getStores().getFirst();        // 사용자의 첫 번째 가게
        testMenu = testStore.getMenus().getFirst();                // 가게의 첫 번째 메뉴
        testMenuCategory = testMenu.getMenuCategory();         // 메뉴의 카테고리
    }

    // 메뉴 생성 성공 케이스 테스트
    @Test
    @DisplayName("메뉴 생성 성공 테스트")  // 테스트 결과에서 보여질 설명
    void createMenu_Success() {
        // Given: 테스트 데이터 및 Mock 설정
        // "주어진 조건" - 테스트를 위한 입력 데이터 준비
        MenuRequestDTO.MenuCreateRequestDTO requestDTO = MenuRequestDTO.MenuCreateRequestDTO.builder()
                .name("신메뉴 BBQ치킨")                      // 생성할 메뉴명
                .content("새로운 BBQ 소스로 맛을 낸 치킨")    // 메뉴 설명
                .price(22000)                               // 가격
                .menuPicture("https://example.com/bbq-chicken.jpg")  // 이미지 URL
                .status(MenuStatus.판매중)                   // 상태
                .category("치킨류")                          // 카테고리
                .build();

        // 예상되는 결과 메뉴 객체 생성
        Menu newMenu = Menu.builder()
                .store(testStore)                           // 테스트용 가게와 연결
                .menuCategory(testMenuCategory)             // 테스트용 카테고리와 연결
                .name("신메뉴 BBQ치킨")
                .price(22000)
                .content("새로운 BBQ 소스로 맛을 낸 치킨")
                .status(MenuStatus.판매중)
                .build();

        // Mock 객체들의 동작 정의 (BDD 스타일)
        // "~가 주어졌을 때 ~를 리턴한다"
        given(storeRepository.findByUserId(testUserWithAll.getId()))
                .willReturn(Optional.of(testStore));       // 사용자 ID로 가게 찾기 성공

        given(menuCategoryRepository.findByCategory("치킨류"))
                .willReturn(Optional.of(testMenuCategory)); // 카테고리 찾기 성공

        given(menuRepository.existsByNameAndStoreId(requestDTO.getName(), testStore.getId()))
                .willReturn(false);                        // 메뉴명 중복 없음

        given(menuRepository.save(any(Menu.class)))
                .willReturn(newMenu);                      // 메뉴 저장 성공

        // When: 테스트 대상 메서드 호출
        // "언제" - 실제로 테스트할 메소드 실행
        MenuResponseDTO.MenuDetailResponseDTO result = menuCommandService.createMenu(requestDTO, testUserWithAll);

        // Then: 결과 검증
        // "그러면" - 기대하는 결과가 나왔는지 확인
        assertThat(result).isNotNull();                    // 결과가 null이 아닌지 확인
        assertThat(result.getName()).isEqualTo("신메뉴 BBQ치킨");       // 이름이 올바른지 확인
        assertThat(result.getPrice()).isEqualTo(22000);                 // 가격이 올바른지 확인
        assertThat(result.getStatus()).isEqualTo(MenuStatus.판매중);     // 상태가 올바른지 확인
        assertThat(result.getStoreId()).isEqualTo(testStore.getId());   // 가게 ID가 올바른지 확인
        assertThat(result.getCategory()).isEqualTo("치킨류");            // 카테고리가 올바른지 확인

        // Mock 객체들이 정확히 호출되었는지 검증
        verify(storeRepository, times(1)).findByUserId(testUserWithAll.getId());  // 1번만 호출되었는지 확인
        verify(menuCategoryRepository, times(1)).findByCategory("치킨류");         // 1번만 호출되었는지 확인
        verify(menuRepository, times(1)).existsByNameAndStoreId(requestDTO.getName(), testStore.getId());  // 1번만 호출
        verify(menuRepository, times(1)).save(any(Menu.class));                    // 1번만 호출되었는지 확인
    }

    // 가게를 찾을 수 없는 예외 상황 테스트
    @Test
    @DisplayName("가게를 찾을 수 없는 경우 예외 발생")
    void createMenu_StoreNotFound_ThrowsException() {
        // Given: 테스트 데이터 및 Mock 설정
        MenuRequestDTO.MenuCreateRequestDTO requestDTO = MenuRequestDTO.MenuCreateRequestDTO.builder()
                .name("신메뉴 BBQ치킨")
                .content("새로운 BBQ 소스로 맛을 낸 치킨")
                .price(22000)
                .category("치킨류")
                .build();

        // 다른 사용자 생성 (가게 소유자가 아닌)
        User differentUser = TestFixtureFactory.createMockUserWithAll();

        // 가게를 찾을 수 없는 상황 설정
        given(storeRepository.findByUserId(differentUser.getId()))
                .willReturn(Optional.empty());  // 빈 Optional 리턴 = 가게 없음

        // When & Then: 예외 발생 검증
        // 메소드 호출 시 예외가 발생하는지 확인
        assertThatThrownBy(() -> menuCommandService.createMenu(requestDTO, differentUser))
                .isInstanceOf(MenuException.class);              // MenuException이 발생하는지 확인

        // 올바른 순서로 호출되었는지 확인
        verify(storeRepository, times(1)).findByUserId(differentUser.getId());  // 가게 찾기는 1번 호출
        verify(menuCategoryRepository, never()).findByCategory(anyString());    // 카테고리 찾기는 호출 안됨
        verify(menuRepository, never()).save(any(Menu.class));                  // 저장은 호출 안됨
    }

    // 중복 메뉴명 예외 상황 테스트
    @Test
    @DisplayName("중복 메뉴명인 경우 예외 발생")
    void createMenu_DuplicateMenuName_ThrowsException() {
        // Given: 기존에 있는 메뉴와 같은 이름으로 생성 시도
        MenuRequestDTO.MenuCreateRequestDTO requestDTO = MenuRequestDTO.MenuCreateRequestDTO.builder()
                .name("후라이드치킨")  // TestFixtureFactory에서 이미 생성된 메뉴명과 동일
                .content("이름만 같은 다른 후라이드치킨")
                .price(19000)
                .category("치킨류")
                .build();

        // 정상적으로 가게와 카테고리는 찾아지지만
        given(storeRepository.findByUserId(testUserWithAll.getId()))
                .willReturn(Optional.of(testStore));

        given(menuCategoryRepository.findByCategory("치킨류"))
                .willReturn(Optional.of(testMenuCategory));

        // 메뉴명이 중복되는 상황 설정
        given(menuRepository.existsByNameAndStoreId("후라이드치킨", testStore.getId()))
                .willReturn(true);  // true = 이미 존재함

        // When & Then: 중복 예외 발생 검증
        assertThatThrownBy(() -> menuCommandService.createMenu(requestDTO, testUserWithAll))
                .isInstanceOf(MenuException.class);             // MenuException 발생 확인

        // 중복 체크에서 실패했으므로 저장은 호출되지 않음
        verify(menuRepository, never()).save(any(Menu.class));
    }

    // 메뉴 수정 성공 테스트
    @Test
    @DisplayName("메뉴 수정 성공 테스트")
    void updateMenu_Success() {
        // Given: 수정할 메뉴 ID와 수정 데이터 준비
        UUID menuId = testMenu.getId();
        MenuRequestDTO.MenuUpdateRequestDTO requestDTO = MenuRequestDTO.MenuUpdateRequestDTO.builder()
                .name("프리미엄 후라이드치킨")      // 새로운 이름
                .price(20000)                    // 새로운 가격
                .content("더욱 바삭해진 프리미엄 치킨")  // 새로운 설명
                .build();

        // Mock 객체들을 더 정교하게 설정
        // 실제 Entity 대신 Mock을 사용해서 더 제어하기 쉽게 만듦

        // 메뉴 소유자 설정
        UUID mockUserId = UUID.randomUUID();
        User mockStoreOwner = mock(User.class);
        when(mockStoreOwner.getId()).thenReturn(mockUserId);

        // 가게 Mock 설정
        Store mockStore = mock(Store.class);
        when(mockStore.getUser()).thenReturn(mockStoreOwner);        // 가게 소유자 설정
        when(mockStore.getId()).thenReturn(UUID.randomUUID());
        when(mockStore.getName()).thenReturn("테스트 가게");

        // 메뉴 카테고리 Mock 설정
        MenuCategory mockMenuCategory = mock(MenuCategory.class);
        when(mockMenuCategory.getCategory()).thenReturn("치킨류");

        // 메뉴 Mock 설정 (lenient()는 엄격하지 않게 설정 - 모든 메소드가 호출되지 않아도 됨)
        Menu mockMenu = mock(Menu.class);
        lenient().when(mockMenu.getStore()).thenReturn(mockStore);
        lenient().when(mockMenu.getMenuCategory()).thenReturn(mockMenuCategory);
        lenient().when(mockMenu.getId()).thenReturn(menuId);
        lenient().when(mockMenu.getName()).thenReturn("기존메뉴명");
        lenient().when(mockMenu.getContent()).thenReturn("기존내용");
        lenient().when(mockMenu.getPrice()).thenReturn(18000);
        lenient().when(mockMenu.getMenuPicture()).thenReturn("기존이미지.jpg");
        lenient().when(mockMenu.getStatus()).thenReturn(MenuStatus.판매중);
        lenient().when(mockMenu.getCreatedAt()).thenReturn(java.time.LocalDateTime.now().minusDays(1));
        lenient().when(mockMenu.getUpdatedAt()).thenReturn(java.time.LocalDateTime.now());

        // 권한 체크를 통과할 사용자 (같은 ID로 설정)
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(mockUserId);  // 소유자와 같은 ID

        // 메뉴 찾기 성공 설정
        given(menuRepository.findById(menuId))
                .willReturn(Optional.of(mockMenu));

        // When: 메뉴 수정 메소드 호출
        MenuResponseDTO.MenuDetailResponseDTO result = menuCommandService.updateMenu(menuId, requestDTO, mockUser);

        // Then: 결과 검증
        assertThat(result).isNotNull();                                    // 결과가 null이 아닌지
        assertThat(result.getName()).isEqualTo("프리미엄 후라이드치킨");     // 이름이 변경되었는지
        assertThat(result.getPrice()).isEqualTo(20000);                    // 가격이 변경되었는지
        assertThat(result.getCategory()).isEqualTo("치킨류");               // 카테고리는 유지되는지
        verify(menuRepository, times(1)).findById(menuId);                 // 메뉴 찾기가 1번 호출되었는지
    }

    // 권한 없는 사용자의 수정 시도 테스트
    @Test
    @DisplayName("권한 없는 사용자가 메뉴 수정 시도 시 예외 발생")
    void updateMenu_UnauthorizedUser_ThrowsException() {
        // Given: 권한 없는 사용자와 메뉴 설정
        UUID menuId = UUID.randomUUID();

        // 메뉴 소유자 (진짜 가게 주인)
        UUID ownerId = UUID.randomUUID();
        User owner = mock(User.class);
        when(owner.getId()).thenReturn(ownerId);

        // 가게와 메뉴 설정
        Store mockStore = mock(Store.class);
        when(mockStore.getUser()).thenReturn(owner);  // 가게의 소유자는 owner

        Menu mockMenu = mock(Menu.class);
        when(mockMenu.getStore()).thenReturn(mockStore);

        // 권한이 없는 다른 사용자
        User unauthorizedUser = mock(User.class);
        when(unauthorizedUser.getId()).thenReturn(UUID.randomUUID());  // 다른 ID

        // 수정 요청 데이터
        MenuRequestDTO.MenuUpdateRequestDTO requestDTO = MenuRequestDTO.MenuUpdateRequestDTO.builder()
                .name("해킹 메뉴")  // 해킹 시도
                .build();

        // 메뉴 찾기는 성공하지만
        given(menuRepository.findById(menuId))
                .willReturn(Optional.of(mockMenu));

        // When & Then: 권한 없음 예외 발생 검증
        assertThatThrownBy(() -> menuCommandService.updateMenu(menuId, requestDTO, unauthorizedUser))
                .isInstanceOf(MenuException.class)          // 권한 없음 예외 확인
                .hasMessageContaining("메뉴에 접근할 수 있는 권한이 없습니다");
    }

    // 메뉴 삭제 성공 테스트
    @Test
    @DisplayName("메뉴 삭제 성공 테스트")
    void deleteMenu_Success() {
        // Given: 삭제할 메뉴와 권한 있는 사용자 설정
        UUID menuId = UUID.randomUUID();

        // 메뉴 소유자 설정
        UUID ownerId = UUID.randomUUID();
        User owner = mock(User.class);
        when(owner.getId()).thenReturn(ownerId);

        // 가게와 메뉴 설정
        Store mockStore = mock(Store.class);
        when(mockStore.getUser()).thenReturn(owner);

        Menu mockMenu = mock(Menu.class);
        when(mockMenu.getStore()).thenReturn(mockStore);

        // 삭제를 요청하는 사용자 (소유자와 같은 ID)
        User requestUser = mock(User.class);
        when(requestUser.getId()).thenReturn(ownerId);  // 권한 있음

        // 메뉴 찾기 성공 설정
        given(menuRepository.findById(menuId))
                .willReturn(Optional.of(mockMenu));

        // When & Then: 삭제가 예외 없이 성공하는지 확인
        assertThatCode(() -> menuCommandService.deleteMenu(menuId, requestUser))
                .doesNotThrowAnyException();  // 어떤 예외도 발생하지 않아야 함

        // 메뉴 찾기가 1번 호출되었는지 확인
        verify(menuRepository, times(1)).findById(menuId);
    }

    // 존재하지 않는 메뉴 삭제 시도 테스트
    @Test
    @DisplayName("존재하지 않는 메뉴 삭제 시 예외 발생")
    void deleteMenu_MenuNotFound_ThrowsException() {
        // Given: 존재하지 않는 메뉴 ID
        UUID nonExistentMenuId = UUID.randomUUID();

        // 삭제를 시도하는 사용자
        User requestUser = mock(User.class);

        // 메뉴를 찾을 수 없는 상황 설정
        given(menuRepository.findById(nonExistentMenuId))
                .willReturn(Optional.empty());  // 빈 Optional = 메뉴 없음

        // When & Then: 메뉴 없음 예외 발생 검증
        assertThatThrownBy(() -> menuCommandService.deleteMenu(nonExistentMenuId, requestUser))
                .isInstanceOf(MenuException.class)              // 찾을 수 없음 예외 확인
                .hasMessageContaining("메뉴를 찾을 수 없습니다");    // 예외 메시지 확인

        // 메뉴 찾기가 1번 호출되었는지 확인
        verify(menuRepository, times(1)).findById(nonExistentMenuId);
    }
}
