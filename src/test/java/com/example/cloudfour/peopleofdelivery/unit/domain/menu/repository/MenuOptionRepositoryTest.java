package com.example.cloudfour.peopleofdelivery.unit.domain.menu.repository;

import com.example.cloudfour.peopleofdelivery.domain.menu.entity.Menu;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuCategory;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuOption;
import com.example.cloudfour.peopleofdelivery.domain.menu.enums.MenuStatus;
import com.example.cloudfour.peopleofdelivery.domain.menu.repository.MenuOptionRepository;
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
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@DisplayName("메뉴 옵션 레포지토리 테스트")
class MenuOptionRepositoryTest {

    @Autowired
    private MenuOptionRepository menuOptionRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Menu menu;
    private MenuOption option1;

    @BeforeEach
    void setUp() {
        Region region = Factory.createMockRegion();
        entityManager.persistAndFlush(region);

        StoreCategory storeCategory = Factory.createMockStoreCategory();
        entityManager.persistAndFlush(storeCategory);

        User user = Factory.createMockUserWithRole(Role.OWNER, region);
        entityManager.persistAndFlush(user);

        Store store = Store.builder()
                .name("테스트 매장")
                .address("서울시 강남구")
                .phone("02-1234-5678")
                .content("테스트 매장입니다")
                .minPrice(10000)
                .deliveryTip(3000)
                .operationHours("09:00-22:00")
                .closedDays("연중무휴")
                .user(user)
                .storeCategory(storeCategory)
                .region(region)
                .build();
        entityManager.persistAndFlush(store);

        MenuCategory menuCategory = Factory.createMockMenuCategory();
        entityManager.persistAndFlush(menuCategory);

        menu = Menu.builder()
                .name("후라이드치킨")
                .content("기본 치킨")
                .price(18000)
                .status(MenuStatus.판매중)
                .menuCategory(menuCategory)
                .store(store)
                .build();
        entityManager.persistAndFlush(menu);

        option1 = MenuOption.builder()
                .optionName("양념소스")
                .additionalPrice(1000)
                .menu(menu)
                .build();

        MenuOption option2 = MenuOption.builder()
                .optionName("치즈 추가")
                .additionalPrice(500)
                .menu(menu)
                .build();

        MenuOption option3 = MenuOption.builder()
                .optionName("매운맛")
                .additionalPrice(0)
                .menu(menu)
                .build();

        entityManager.persistAndFlush(option1);
        entityManager.persistAndFlush(option2);
        entityManager.persistAndFlush(option3);
        entityManager.clear();
    }

    @Test
    @DisplayName("메뉴 ID로 메뉴 옵션 조회 - 추가 가격 순으로 정렬")
    void findByMenuIdOrderByAdditionalPrice_Success() {
        List<MenuOption> options = menuOptionRepository.findByMenuIdOrderByAdditionalPrice(menu.getId());

        assertThat(options).hasSize(3);
        assertThat(options).extracting("additionalPrice").containsExactly(0, 500, 1000);
        assertThat(options).extracting("optionName").containsExactly("매운맛", "치즈 추가", "양념소스");
    }

    @Test
    @DisplayName("메뉴 ID로 메뉴 옵션 조회 - ID 순으로 정렬")
    void findByMenuIdOrderById_Success() {
        List<MenuOption> options = menuOptionRepository.findByMenuIdOrderById(menu.getId());

        assertThat(options).hasSize(3);
        assertThat(options.get(0).getId()).isNotNull();
        assertThat(options.get(1).getId()).isNotNull();
        assertThat(options.get(2).getId()).isNotNull();
    }

    @Test
    @DisplayName("메뉴 ID와 옵션명으로 존재 여부 확인 - 존재하는 경우")
    void existsByMenuIdAndOptionName_ExistsTrue() {
        boolean exists = menuOptionRepository.existsByMenuIdAndOptionName(menu.getId(), "양념소스");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("메뉴 ID와 옵션명으로 존재 여부 확인 - 존재하지 않는 경우")
    void existsByMenuIdAndOptionName_ExistsFalse() {
        boolean exists = menuOptionRepository.existsByMenuIdAndOptionName(menu.getId(), "존재하지않는옵션");

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("옵션 ID로 메뉴와 함께 조회 - 존재하는 경우")
    void findByIdWithMenu_ExistsSuccess() {
        Optional<MenuOption> result = menuOptionRepository.findByIdWithMenu(option1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getOptionName()).isEqualTo("양념소스");
        assertThat(result.get().getMenu()).isNotNull();
        assertThat(result.get().getMenu().getName()).isEqualTo("후라이드치킨");
    }

    @Test
    @DisplayName("옵션 ID로 메뉴와 함께 조회 - 존재하지 않는 경우")
    void findByIdWithMenu_NotExists() {
        Optional<MenuOption> result = menuOptionRepository.findByIdWithMenu(java.util.UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 메뉴 ID로 옵션 조회")
    void findByMenuIdOrderByAdditionalPrice_NonExistentMenu() {
        List<MenuOption> options = menuOptionRepository.findByMenuIdOrderByAdditionalPrice(java.util.UUID.randomUUID());

        assertThat(options).isEmpty();
    }

    @Test
    @DisplayName("새 메뉴 옵션 저장")
    void save_Success() {
        MenuOption newOption = MenuOption.builder()
                .optionName("라면사리 추가")
                .additionalPrice(2000)
                .menu(menu)
                .build();

        MenuOption savedOption = menuOptionRepository.save(newOption);

        assertThat(savedOption.getId()).isNotNull();
        assertThat(savedOption.getOptionName()).isEqualTo("라면사리 추가");
        assertThat(savedOption.getAdditionalPrice()).isEqualTo(2000);

        List<MenuOption> allOptions = menuOptionRepository.findByMenuIdOrderByAdditionalPrice(menu.getId());
        assertThat(allOptions).hasSize(4);
    }

    @Test
    @DisplayName("메뉴 옵션 삭제")
    void delete_Success() {
        long initialCount = menuOptionRepository.count();

        menuOptionRepository.delete(option1);

        long finalCount = menuOptionRepository.count();
        assertThat(finalCount).isEqualTo(initialCount - 1);

        boolean exists = menuOptionRepository.existsByMenuIdAndOptionName(menu.getId(), "양념소스");
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("모든 메뉴 옵션 조회")
    void findAll_Success() {
        List<MenuOption> allOptions = menuOptionRepository.findAll();

        assertThat(allOptions).hasSize(3);
        assertThat(allOptions).extracting("optionName")
                .containsExactlyInAnyOrder("양념소스", "치즈 추가", "매운맛");
    }

    @Test
    @DisplayName("옵션명 대소문자 구분 확인")
    void existsByMenuIdAndOptionName_CaseSensitive() {
        boolean exists1 = menuOptionRepository.existsByMenuIdAndOptionName(menu.getId(), "양념소스");
        boolean exists2 = menuOptionRepository.existsByMenuIdAndOptionName(menu.getId(), "SAUCE");

        assertThat(exists1).isTrue();
        assertThat(exists2).isFalse();
    }

    @Test
    @DisplayName("메뉴별 옵션 중복 검증 - 같은 메뉴 내 중복, 다른 메뉴는 허용")
    void checkDuplicateOptionName() {
        String duplicateOptionName = "양념소스";

        boolean exists = menuOptionRepository.existsByMenuIdAndOptionName(menu.getId(), duplicateOptionName);

        assertThat(exists).isTrue();

        Menu anotherMenu = Menu.builder()
                .name("간장치킨")
                .content("달콤한 간장치킨")
                .price(19000)
                .status(MenuStatus.판매중)
                .menuCategory(menu.getMenuCategory())
                .store(menu.getStore())
                .build();
        entityManager.persistAndFlush(anotherMenu);

        boolean existsInAnotherMenu = menuOptionRepository.existsByMenuIdAndOptionName(anotherMenu.getId(), duplicateOptionName);
        assertThat(existsInAnotherMenu).isFalse();
    }
}
