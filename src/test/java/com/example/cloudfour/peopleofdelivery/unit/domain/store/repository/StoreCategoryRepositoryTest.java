package com.example.cloudfour.peopleofdelivery.unit.domain.store.repository;

import com.example.cloudfour.peopleofdelivery.domain.store.entity.StoreCategory;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreCategoryRepository;
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
@DisplayName("가게 카테고리 레포지토리 테스트")
class StoreCategoryRepositoryTest {

    @Autowired
    private StoreCategoryRepository storeCategoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private StoreCategory koreanCategory;

    @BeforeEach
    void setUp() {
        
        koreanCategory = StoreCategory.builder()
                .category("한식")
                .build();

        StoreCategory chineseCategory = StoreCategory.builder()
                .category("중식")
                .build();

        StoreCategory westernCategory = StoreCategory.builder()
                .category("양식")
                .build();

        entityManager.persistAndFlush(koreanCategory);
        entityManager.persistAndFlush(chineseCategory);
        entityManager.persistAndFlush(westernCategory);
        entityManager.clear();
    }

    @Test
    @DisplayName("카테고리명으로 가게 카테고리 조회 - 존재하는 경우")
    void findByCategory_ExistsSuccess() {
        
        Optional<StoreCategory> result = storeCategoryRepository.findByCategory("한식");

        
        assertThat(result).isPresent();
        assertThat(result.get().getCategory()).isEqualTo("한식");
        assertThat(result.get().getId()).isEqualTo(koreanCategory.getId());
    }

    @Test
    @DisplayName("카테고리명으로 가게 카테고리 조회 - 존재하지 않는 경우")
    void findByCategory_NotExists() {
        
        Optional<StoreCategory> result = storeCategoryRepository.findByCategory("일식");

        
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("카테고리명으로 가게 카테고리 조회 - 대소문자 구분")
    void findByCategory_CaseSensitive() {
        
        Optional<StoreCategory> result1 = storeCategoryRepository.findByCategory("한식");
        Optional<StoreCategory> result2 = storeCategoryRepository.findByCategory("HANSHIK");

        
        assertThat(result1).isPresent();
        assertThat(result2).isEmpty();
    }

    @Test
    @DisplayName("모든 가게 카테고리 조회")
    void findAll_Success() {
        
        List<StoreCategory> categories = storeCategoryRepository.findAll();

        
        assertThat(categories).hasSize(3);
        assertThat(categories).extracting("category")
                .containsExactlyInAnyOrder("한식", "중식", "양식");
    }

    @Test
    @DisplayName("가게 카테고리 저장")
    void save_Success() {
        
        StoreCategory newCategory = StoreCategory.builder()
                .category("일식")
                .build();

        
        StoreCategory savedCategory = storeCategoryRepository.save(newCategory);

        
        assertThat(savedCategory.getId()).isNotNull();
        assertThat(savedCategory.getCategory()).isEqualTo("일식");

        
        Optional<StoreCategory> foundCategory = storeCategoryRepository.findByCategory("일식");
        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getId()).isEqualTo(savedCategory.getId());
    }

    @Test
    @DisplayName("가게 카테고리 삭제")
    void delete_Success() {
        
        long initialCount = storeCategoryRepository.count();

        
        storeCategoryRepository.delete(koreanCategory);

        
        long finalCount = storeCategoryRepository.count();
        assertThat(finalCount).isEqualTo(initialCount - 1);

        Optional<StoreCategory> deletedCategory = storeCategoryRepository.findByCategory("한식");
        assertThat(deletedCategory).isEmpty();
    }

    @Test
    @DisplayName("존재 여부 확인 - ID로")
    void existsById_Success() {
        
        boolean exists = storeCategoryRepository.existsById(koreanCategory.getId());
        boolean notExists = storeCategoryRepository.existsById(java.util.UUID.randomUUID());

        
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("가게 카테고리 수정")
    void update_Success() {
        
        StoreCategory categoryToUpdate = storeCategoryRepository.findByCategory("한식").orElseThrow();

        
        entityManager.getEntityManager()
                .createQuery("UPDATE StoreCategory sc SET sc.category = :newCategory WHERE sc.id = :id")
                .setParameter("newCategory", "전통한식")
                .setParameter("id", categoryToUpdate.getId())
                .executeUpdate();
        entityManager.flush();

        
        Optional<StoreCategory> foundCategory = storeCategoryRepository.findByCategory("전통한식");
        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getId()).isEqualTo(categoryToUpdate.getId());

        Optional<StoreCategory> oldCategory = storeCategoryRepository.findByCategory("한식");
        assertThat(oldCategory).isEmpty();
    }

    @Test
    @DisplayName("중복된 카테고리명 저장 시도")
    void save_DuplicateCategory() {
        
        StoreCategory duplicateCategory = StoreCategory.builder()
                .category("한식")
                .build();

        
        
        StoreCategory savedCategory = storeCategoryRepository.save(duplicateCategory);
        assertThat(savedCategory.getId()).isNotNull();
        assertThat(savedCategory.getCategory()).isEqualTo("한식");
    }

    @Test
    @DisplayName("빈 문자열로 카테고리 조회")
    void findByCategory_EmptyString() {
        
        Optional<StoreCategory> result = storeCategoryRepository.findByCategory("");

        
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("null로 카테고리 조회")
    void findByCategory_Null() {
        
        Optional<StoreCategory> result = storeCategoryRepository.findByCategory(null);

        
        assertThat(result).isEmpty();
    }
}
