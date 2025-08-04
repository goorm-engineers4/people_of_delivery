package com.example.cloudfour.peopleofdelivery.unit.domain.menu.repository;

import com.example.cloudfour.peopleofdelivery.domain.menu.entity.Menu;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuCategory;
import com.example.cloudfour.peopleofdelivery.domain.menu.enums.MenuStatus;
import com.example.cloudfour.peopleofdelivery.domain.menu.repository.MenuRepository;
import com.example.cloudfour.peopleofdelivery.domain.region.entity.Region;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.StoreCategory;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.Role;
import com.example.cloudfour.peopleofdelivery.fixtures.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@DisplayName("메뉴 레포지토리 테스트")
class MenuRepositoryTest {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Store store;
    private MenuCategory menuCategory;

    @BeforeEach
    void setUp() {
        Region region = Factory.createMockRegion();
        entityManager.persistAndFlush(region);

        StoreCategory storeCategory = Factory.createMockStoreCategory();
        entityManager.persistAndFlush(storeCategory);

        User user = Factory.createMockUserWithRoleAndRegion(Role.OWNER, region);
        entityManager.persistAndFlush(user);

        store = Store.builder()
                .name("맛있는치킨")
                .address("서울시 종로구 청운동 200")
                .phone("02-111-2222")
                .content("바삭하고 맛있는 치킨")
                .minPrice(15000)
                .deliveryTip(2000)
                .operationHours("10:00 - 22:00")
                .closedDays("매주 월요일")
                .user(user)
                .storeCategory(storeCategory)
                .region(region)
                .build();
        entityManager.persistAndFlush(store);

        menuCategory = Factory.createMockMenuCategory();
        entityManager.persistAndFlush(menuCategory);

        Menu menu1 = Menu.builder()
                .name("후라이드치킨")
                .content("기본 치킨")
                .price(18000)
                .status(MenuStatus.판매중)
                .menuCategory(menuCategory)
                .store(store)
                .build();

        Menu menu2 = Menu.builder()
                .name("양념치킨")
                .content("달콤한 양념치킨")
                .price(19000)
                .status(MenuStatus.판매중)
                .menuCategory(menuCategory)
                .store(store)
                .build();

        entityManager.persistAndFlush(menu1);
        entityManager.persistAndFlush(menu2);
        entityManager.clear();
    }

    @Test
    @DisplayName("매장별 메뉴 조회 - 삭제되지 않은 메뉴만")
    void findByStoreIdAndDeletedFalseOrderByCreatedAtDesc_Success() {
        List<Menu> menus = menuRepository.findByStoreIdAndDeletedFalseOrderByCreatedAtDesc(store.getId());

        assertThat(menus).hasSize(2);
        assertThat(menus).extracting("name").contains("후라이드치킨", "양념치킨");
    }

    @Test
    @DisplayName("매장 ID와 커서로 메뉴 페이징 조회")
    void findByStoreIdAndDeletedFalseAndCreatedAtBefore_Success() {
        LocalDateTime cursor = LocalDateTime.now().plusMinutes(1);
        Pageable pageable = PageRequest.of(0, 10);

        Slice<Menu> menuSlice = menuRepository.findByStoreIdAndDeletedFalseAndCreatedAtBefore(
                store.getId(), cursor, pageable);

        assertThat(menuSlice.getContent()).hasSize(2);
        assertThat(menuSlice.hasNext()).isFalse();
    }

    @Test
    @DisplayName("매장 ID, 메뉴 카테고리 ID와 커서로 메뉴 페이징 조회")
    void findByStoreIdAndMenuCategoryIdAndDeletedFalseAndCreatedAtBefore_Success() {
        LocalDateTime cursor = LocalDateTime.now().plusMinutes(1);
        Pageable pageable = PageRequest.of(0, 10);

        Slice<Menu> menuSlice = menuRepository.findByStoreIdAndMenuCategoryIdAndDeletedFalseAndCreatedAtBefore(
                store.getId(), menuCategory.getId(), cursor, pageable);

        assertThat(menuSlice.getContent()).hasSize(2);
        assertThat(menuSlice.hasNext()).isFalse();
    }

    @Test
    @DisplayName("메뉴명과 매장 ID로 중복 확인 - 존재하는 경우")
    void existsByNameAndStoreId_ExistsTrue() {
        boolean exists = menuRepository.existsByNameAndStoreId("후라이드치킨", store.getId());

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("메뉴명과 매장 ID로 중복 확인 - 존재하지 않는 경우")
    void existsByNameAndStoreId_ExistsFalse() {
        boolean exists = menuRepository.existsByNameAndStoreId("불고기", store.getId());

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("인기 메뉴 조회 - 주문 수 기준 TOP 목록")
    void findTopMenusByOrderCount_Success() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Menu> topMenus = menuRepository.findTopMenusByOrderCount(pageable);

        assertThat(topMenus).hasSize(2);
    }

    @Test
    @DisplayName("시간대별 인기 메뉴 조회 - 특정 기간 내 주문 기준")
    void findTopMenusByTimeRange_Success() {
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now().plusDays(1);
        Pageable pageable = PageRequest.of(0, 10);

        List<Menu> topMenus = menuRepository.findTopMenusByTimeRange(startTime, endTime, pageable);

        assertThat(topMenus).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 매장 ID로 메뉴 조회")
    void findByStoreIdAndDeletedFalseOrderByCreatedAtDesc_NonExistentStore() {
        List<Menu> menus = menuRepository.findByStoreIdAndDeletedFalseOrderByCreatedAtDesc(java.util.UUID.randomUUID());

        assertThat(menus).isEmpty();
    }

    @Test
    @DisplayName("다른 카테고리로 메뉴 필터링 - 빈 결과 확인")
    void findByStoreIdAndMenuCategoryIdAndDeletedFalseAndCreatedAtBefore_DifferentCategory() {
        MenuCategory differentCategory = MenuCategory.builder()
                .category("중식")
                .build();
        entityManager.persistAndFlush(differentCategory);

        LocalDateTime cursor = LocalDateTime.now().plusMinutes(1);
        Pageable pageable = PageRequest.of(0, 10);

        Slice<Menu> menuSlice = menuRepository.findByStoreIdAndMenuCategoryIdAndDeletedFalseAndCreatedAtBefore(
                store.getId(), differentCategory.getId(), cursor, pageable);

        assertThat(menuSlice.getContent()).isEmpty();
    }
}
