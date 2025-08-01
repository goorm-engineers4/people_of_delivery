package com.example.cloudfour.peopleofdelivery.unit.domain.menu.service.query;

import com.example.cloudfour.peopleofdelivery.mock.TestFixtureFactory;
import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.Menu;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuCategory;
import com.example.cloudfour.peopleofdelivery.domain.menu.enums.MenuStatus;
import com.example.cloudfour.peopleofdelivery.domain.menu.exception.MenuException;
import com.example.cloudfour.peopleofdelivery.domain.menu.repository.MenuRepository;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

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

// Query 서비스 테스트 클래스 선언
@ExtendWith(MockitoExtension.class)  // Mockito와 JUnit 5 연동
@DisplayName("메뉴 Query 서비스 테스트")  // 테스트 실행 시 표시될 이름
class MenuQueryServiceImplTest {

    // Mock 객체 - 실제 Repository 대신 가짜 객체 사용
    @Mock  // 가짜 MenuRepository
    private MenuRepository menuRepository;

    // 실제 테스트 대상 - Mock Repository를 주입받음
    @InjectMocks  // MenuQueryServiceImpl에 위의 Mock Repository 주입
    private MenuQueryServiceImpl menuQueryService;

    // 테스트에서 공통으로 사용할 데이터들
    private User testUserWithAll;         // 테스트용 사용자 (전체 정보 포함)
    private Store testStore;              // 테스트용 가게
    private MenuCategory testMenuCategory; // 테스트용 메뉴 카테고리
    private Menu testMenu;                // 테스트용 메뉴

    // 각 테스트 실행 전마다 호출되는 초기화 메소드
    @BeforeEach  // 모든 @Test 메소드 실행 전에 매번 호출
    void setUp() {
        // TestFixtureFactory를 활용하여 완전한 테스트 데이터 생성
        testUserWithAll = TestFixtureFactory.createMockUserWithAll();

        // 연관관계를 통해 필요한 객체들 추출
        testStore = testUserWithAll.getStores().get(0);        // 사용자의 첫 번째 가게
        testMenu = testStore.getMenus().get(0);                // 가게의 첫 번째 메뉴
        testMenuCategory = testMenu.getMenuCategory();         // 메뉴의 카테고리
    }

    // 특정 가게의 메뉴 목록 조회 성공 테스트
    @Test
    @DisplayName("특정 가게의 메뉴 목록 조회 성공")
    void getMenusByStore_Success() {
        // Given: 테스트 데이터 및 Mock 설정
        UUID storeId = testStore.getId();  // 테스트할 가게 ID

        // 여러 개의 가짜 메뉴 리스트 생성
        List<Menu> mockMenus = Arrays.asList(
                createMockMenu("황금올리브치킨", 18000),    // 첫 번째 메뉴
                createMockMenu("마르게리타피자", 25000),    // 두 번째 메뉴
                createMockMenu("불고기버거", 12000)        // 세 번째 메뉴
        );

        // Mock Repository의 동작 정의
        // "storeId로 활성화된 메뉴를 생성일 역순으로 조회"하면 위의 mockMenus를 반환
        given(menuRepository.findByStoreIdAndDeletedFalseOrderByCreatedAtDesc(storeId))
                .willReturn(mockMenus);

        // When: 테스트 대상 메서드 호출
        // 실제로 테스트하고 싶은 메소드 실행
        List<MenuResponseDTO.MenuListResponseDTO> result = menuQueryService.getMenusByStore(storeId);

        // Then: 결과 검증
        assertThat(result).hasSize(3);  // 결과 리스트가 3개인지 확인

        // 메뉴 이름들이 정확한 순서로 나왔는지 확인
        assertThat(result)
                .extracting(MenuResponseDTO.MenuListResponseDTO::getName)  // 이름만 추출
                .containsExactly("황금올리브치킨", "마르게리타피자", "불고기버거");  // 정확한 순서로 포함

        // 메뉴 가격들이 정확한지 확인
        assertThat(result)
                .extracting(MenuResponseDTO.MenuListResponseDTO::getPrice)  // 가격만 추출
                .containsExactly(18000, 25000, 12000);  // 각각의 가격이 맞는지

        // Mock Repository가 정확히 1번 호출되었는지 검증
        verify(menuRepository, times(1)).findByStoreIdAndDeletedFalseOrderByCreatedAtDesc(storeId);
    }

    // 메뉴가 없는 가게 조회 테스트
    @Test
    @DisplayName("빈 가게의 메뉴 목록 조회")
    void getMenusByStore_EmptyStore_ReturnsEmptyList() {
        // Given: 메뉴가 없는 가게 설정
        UUID emptyStoreId = UUID.randomUUID();  // 새로운 가��� ID (메뉴 없음)

        // Mock Repository가 빈 리스트를 반환하도록 설정
        given(menuRepository.findByStoreIdAndDeletedFalseOrderByCreatedAtDesc(emptyStoreId))
                .willReturn(Arrays.asList());  // 빈 리스트 반환

        // When: 메뉴 조회 실행
        List<MenuResponseDTO.MenuListResponseDTO> result = menuQueryService.getMenusByStore(emptyStoreId);

        // Then: 빈 결과 검증
        assertThat(result).isEmpty();  // 결과가 비어있는지 확인

        // Repository가 정확히 호출되었는지 검증
        verify(menuRepository, times(1)).findByStoreIdAndDeletedFalseOrderByCreatedAtDesc(emptyStoreId);
    }

    // 전체 인기 메뉴 TOP20 조회 성공 테스트
    @Test
    @DisplayName("인기 메뉴 TOP20 조회 성공")
    void getTopMenus_Success() {
        // Given: 인기 메뉴 20개 생성
        // IntStream.range(1, 21)는 1부터 20까지의 숫자 생성
        List<Menu> mockTopMenus = IntStream.range(1, 21)
                .mapToObj(i -> createMockMenu(i + "위 인기메뉴", 15000 + i * 1000))  // 각 순위별 메뉴 생성
                .collect(Collectors.toList());  // List로 변환

        // Mock Repository 설정 - 페이징으로 TOP 20 조회
        given(menuRepository.findTopMenusByOrderCount(PageRequest.of(0, 20)))  // 첫 페이지, 20개씩
                .willReturn(mockTopMenus);

        // When: 인기 메뉴 조회 실행
        List<MenuResponseDTO.MenuTopResponseDTO> result = menuQueryService.getTopMenus();

        // Then: 결과 검증
        assertThat(result).hasSize(20);  // 정확히 20개인지 확인

        // 1위 메뉴 검증
        assertThat(result.get(0).getName()).isEqualTo("1위 인기메뉴");
        assertThat(result.get(0).getPrice()).isEqualTo(16000);  // 15000 + 1 * 1000

        // 20위 메뉴 검증
        assertThat(result.get(19).getName()).isEqualTo("20위 인기메뉴");
        assertThat(result.get(19).getPrice()).isEqualTo(35000);  // 15000 + 20 * 1000

        // Repository 호출 검증
        verify(menuRepository, times(1)).findTopMenusByOrderCount(PageRequest.of(0, 20));
    }

    // 시간대별 인기 메뉴 조회 테스트
    @Test
    @DisplayName("시간대별 인기 메뉴 TOP20 조회 성공")
    void getTimeTopMenus_Success() {
        // Given: 시간대별 인기 메뉴 20개 생성
        List<Menu> mockTimeTopMenus = IntStream.range(1, 21)
                .mapToObj(i -> createMockMenu("시간대별 " + i + "위 메뉴", 12000 + i * 500))  // 시간대별 메뉴
                .collect(Collectors.toList());

        // Mock Repository 설정 - any()는 어떤 매개변수든 상관없음
        given(menuRepository.findTopMenusByTimeRange(any(), any(), any()))  // 시작시간, 끝시간, 페이징
                .willReturn(mockTimeTopMenus);

        // When: 시간대별 인기 메뉴 조회
        List<MenuResponseDTO.MenuTimeTopResponseDTO> result = menuQueryService.getTimeTopMenus();

        // Then: 결과 검증
        assertThat(result).hasSize(20);
        assertThat(result.get(0).getName()).isEqualTo("시간대별 1위 메뉴");
        assertThat(result.get(0).getPrice()).isEqualTo(12500);  // 12000 + 1 * 500

        verify(menuRepository, times(1)).findTopMenusByTimeRange(any(), any(), any());
    }

    // 지역별 인기 메뉴 조회 테스트
    @Test
    @DisplayName("지역별 인기 메뉴 TOP20 조회 성공")
    void getRegionTopMenus_Success() {
        // Given: 지역별 인기 메뉴 20개 생성
        String si = "서울특별시";
        String gu = "강남구";
        List<Menu> mockRegionTopMenus = IntStream.range(1, 21)
                .mapToObj(i -> createMockMenu("지역별 " + i + "위 메뉴", 10000 + i * 800))  // 지역별 메뉴
                .collect(Collectors.toList());

        // Mock Repository 설정
        given(menuRepository.findTopMenusByRegion(eq(si), eq(gu), any()))  // si, gu, 페이징
                .willReturn(mockRegionTopMenus);

        // When: 지역별 인기 메뉴 조회
        List<MenuResponseDTO.MenuRegionTopResponseDTO> result = menuQueryService.getRegionTopMenus(si, gu);

        // Then: 결과 검증
        assertThat(result).hasSize(20);
        assertThat(result.get(0).getName()).isEqualTo("지역별 1위 메뉴");
        assertThat(result.get(0).getPrice()).isEqualTo(10800);  // 10000 + 1 * 800

        verify(menuRepository, times(1)).findTopMenusByRegion(eq(si), eq(gu), any());
    }

    // 메뉴 상세 조회 성공 테스트
    @Test
    @DisplayName("메뉴 상세 조회 성공")
    void getMenuDetail_Success() {
        // Given: 조회할 메뉴 설정
        UUID menuId = testMenu.getId();  // TestFixtureFactory에서 생성된 메뉴 ID

        // Mock Repository가 해당 메뉴를 반환하도록 설정
        given(menuRepository.findById(menuId))
                .willReturn(Optional.of(testMenu));  // Optional로 감싸서 반환

        // When: 메뉴 상세 조회 실행
        MenuResponseDTO.MenuDetailResponseDTO result = menuQueryService.getMenuDetail(menuId);

        // Then: 결과 검증 - TestFixtureFactory에서 설정한 값들과 비교
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("후라이드치킨");    // Factory에서 설정한 이름
        assertThat(result.getPrice()).isEqualTo(18000);          // Factory에서 설정한 가격
        assertThat(result.getContent()).isEqualTo("기본 치킨");   // Factory에서 설정한 설명
        assertThat(result.getStatus()).isEqualTo(MenuStatus.판매중);  // Factory에서 설정한 상태

        // Repository 호출 검증
        verify(menuRepository, times(1)).findById(menuId);
    }

    // 존재하지 않는 메뉴 조회 시 예외 발생 테스트
    @Test
    @DisplayName("존재하지 않는 메뉴 상세 조회 시 예외 발생")
    void getMenuDetail_MenuNotFound_ThrowsException() {
        // Given: 존재하지 않는 메뉴 ID
        UUID nonExistentMenuId = UUID.randomUUID();  // 임의의 새 UUID

        // Mock Repository가 빈 Optional을 반환하도록 설정 (메뉴 없음을 의미)
        given(menuRepository.findById(nonExistentMenuId))
                .willReturn(Optional.empty());  // 빈 Optional = 메뉴 없음

        // When & Then: 예외 발생 검증
        // 메소드 호출 시 MenuException이 발생하는지 확인
        assertThatThrownBy(() -> menuQueryService.getMenuDetail(nonExistentMenuId))
                .isInstanceOf(MenuException.class);              // 올바른 예외 타입인지

        // Repository가 정확히 호출되었는지 검증
        verify(menuRepository, times(1)).findById(nonExistentMenuId);
    }

    // 테스트용 메뉴 생성 헬퍼 메소드 (TestFixtureFactory 패턴 활용)
    private Menu createMockMenu(String name, Integer price) {
        // 기본적인 메뉴 객체 생성
        Menu menu = Menu.builder()
                .store(testStore)                      // 미리 설정된 테스트용 가게
                .menuCategory(testMenuCategory)        // 미리 설정된 테스트용 카테고리
                .name(name)                            // 매개변수로 받은 이름
                .price(price)                          // 매개변수로 받은 가격
                .content(name + " 설명")               // 이름 + "설명"으로 자동 생성
                .status(MenuStatus.판매중)              // 기본적으로 판매중 상태
                .build();

        // TestFixtureFactory 패턴처럼 양방향 연관관계 설정
        // 이렇게 해야 실제 Entity처럼 동작함
        testMenuCategory.getMenus().add(menu);     // 카테고리의 메뉴 리스트에 추가
        testStore.getMenus().add(menu);            // 가게의 메뉴 리스트에 추가

        return menu;  // 완성된 메뉴 반환
    }
}
