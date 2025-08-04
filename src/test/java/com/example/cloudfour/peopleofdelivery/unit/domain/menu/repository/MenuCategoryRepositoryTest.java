package com.example.cloudfour.peopleofdelivery.unit.domain.menu.repository;

import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuCategory;
import com.example.cloudfour.peopleofdelivery.domain.menu.repository.MenuCategoryRepository;
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
@DisplayName("메뉴 카테고리 레포지토리 테스트")
class MenuCategoryRepositoryTest {

    @Autowired
    private MenuCategoryRepository menuCategoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private MenuCategory koreanCategory;

    @BeforeEach
    void setUp() {
        koreanCategory = Factory.createMockMenuCategory();

        MenuCategory chineseCategory = MenuCategory.builder()
                .category("중식")
                .build();

        entityManager.persistAndFlush(koreanCategory);
        entityManager.persistAndFlush(chineseCategory);
        entityManager.clear();
    }

    @Test
    @DisplayName("카테고리명으로 메뉴 카테고리 조회 - 존재하는 경우")
    void findByCategory_ExistsSuccess() {
        Optional<MenuCategory> result = menuCategoryRepository.findByCategory("치킨류");

        assertThat(result).isPresent();
        assertThat(result.get().getCategory()).isEqualTo("치킨류");
        assertThat(result.get().getId()).isEqualTo(koreanCategory.getId());
    }

    @Test
    @DisplayName("카테고리명으로 메뉴 카테고리 조회 - 존재하지 않는 경우")
    void findByCategory_NotExists() {
        Optional<MenuCategory> result = menuCategoryRepository.findByCategory("일식");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("카테고리명으로 메뉴 카테고리 조회 - 대소문자 정확히 일치")
    void findByCategory_CaseSensitive() {
        Optional<MenuCategory> result1 = menuCategoryRepository.findByCategory("치킨류");
        Optional<MenuCategory> result2 = menuCategoryRepository.findByCategory("CHICKEN");

        assertThat(result1).isPresent();
        assertThat(result2).isEmpty();
    }

    @Test
    @DisplayName("모든 메뉴 카테고리 조회")
    void findAll_Success() {
        List<MenuCategory> categories = menuCategoryRepository.findAll();

        assertThat(categories).hasSize(2);
        assertThat(categories).extracting("category").containsExactlyInAnyOrder("치킨류", "중식");
    }

    @Test
    @DisplayName("새 카테고리 저장")
    void save_Success() {
        MenuCategory newCategory = MenuCategory.builder()
                .category("양식")
                .build();

        MenuCategory savedCategory = menuCategoryRepository.save(newCategory);

        assertThat(savedCategory.getId()).isNotNull();
        assertThat(savedCategory.getCategory()).isEqualTo("양식");

        Optional<MenuCategory> foundCategory = menuCategoryRepository.findByCategory("양식");
        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getId()).isEqualTo(savedCategory.getId());
    }

    @Test
    @DisplayName("메뉴 카테고리 삭제")
    void delete_Success() {
        long initialCount = menuCategoryRepository.count();

        menuCategoryRepository.delete(koreanCategory);

        long finalCount = menuCategoryRepository.count();
        assertThat(finalCount).isEqualTo(initialCount - 1);

        Optional<MenuCategory> deletedCategory = menuCategoryRepository.findByCategory("치킨류");
        assertThat(deletedCategory).isEmpty();
    }

    @Test
    @DisplayName("카테고리 ID 존재 여부 확인")
    void existsById_Success() {
        boolean exists = menuCategoryRepository.existsById(koreanCategory.getId());
        boolean notExists = menuCategoryRepository.existsById(java.util.UUID.randomUUID());

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("중복 카테고리명 저장 허용 여부")
    void save_DuplicateCategory() {
        MenuCategory duplicateCategory = MenuCategory.builder()
                .category("치킨류")
                .build();

        MenuCategory savedCategory = menuCategoryRepository.save(duplicateCategory);
        assertThat(savedCategory.getId()).isNotNull();
        assertThat(savedCategory.getCategory()).isEqualTo("치킨류");
    }
}
