package com.example.cloudfour.peopleofdelivery.unit.domain.store.repository;

import com.example.cloudfour.peopleofdelivery.domain.region.entity.Region;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.StoreCategory;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.LoginType;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@DisplayName("가게 레포지토리 테스트")
class StoreRepositoryTest {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User ownerUser;
    private User deletedUser;
    private Store mainStore;
    private StoreCategory koreanCategory;
    private StoreCategory chineseCategory;
    private Region region;

    @BeforeEach
    void setUp() {
        
        region = Factory.createMockRegion();
        entityManager.persistAndFlush(region);

        
        koreanCategory = Factory.createMockStoreCategory();
        chineseCategory = StoreCategory.builder()
                .category("중식")
                .build();
        entityManager.persistAndFlush(koreanCategory);
        entityManager.persistAndFlush(chineseCategory);

        
        ownerUser = Factory.createMockUserWithRole(Role.OWNER, region);
        entityManager.persistAndFlush(ownerUser);

        
        deletedUser = User.builder()
                .email("deleted@example.com")
                .nickname("삭제된사장")
                .number("010-9999-9999")
                .password("password")
                .role(Role.OWNER)
                .loginType(LoginType.LOCAL)
                .emailVerified(true)
                .build();
        entityManager.persistAndFlush(deletedUser);

        
        entityManager.getEntityManager()
                .createQuery("UPDATE User u SET u.isDeleted = true WHERE u.id = :id")
                .setParameter("id", deletedUser.getId())
                .executeUpdate();
        entityManager.flush();

        
        mainStore = Store.builder()
                .name("메인 한식집")
                .address("서울시 강남구 역삼동")
                .phone("02-1234-5678")
                .content("정통 한식을 맛볼 수 있는 곳")
                .minPrice(15000)
                .deliveryTip(3000)
                .operationHours("11:00-22:00")
                .closedDays("연중무휴")
                .user(ownerUser)
                .storeCategory(koreanCategory)
                .region(region)
                .build();
        entityManager.persistAndFlush(mainStore);

        
        Store deletedUserStore = Store.builder()
                .name("삭제된 가게")
                .address("서울시 강남구 삼성동")
                .phone("02-3333-3333")
                .content("삭제된 사용자의 가게")
                .minPrice(20000)
                .deliveryTip(4000)
                .operationHours("12:00-23:00")
                .closedDays("일요일")
                .user(deletedUser)
                .storeCategory(koreanCategory)
                .region(region)
                .build();
        entityManager.persistAndFlush(deletedUserStore);

        entityManager.clear();
    }

    @Test
    @DisplayName("사용자 ID로 가게 조회 - 존재하는 경우")
    void findByUserId_ExistsSuccess() {
        
        Optional<Store> result = storeRepository.findByUserId(ownerUser.getId());

        
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("메인 한식집");
        assertThat(result.get().getUser().getId()).isEqualTo(ownerUser.getId());
    }

    @Test
    @DisplayName("사용자 ID로 가게 조회 - 존재하지 않는 경우")
    void findByUserId_NotExists() {
        
        Optional<Store> result = storeRepository.findByUserId(java.util.UUID.randomUUID());

        
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("사용자 ID로 가게 조회 - 삭제된 사용자")
    void findByUserId_DeletedUser() {
        
        Optional<Store> result = storeRepository.findByUserId(deletedUser.getId());

        
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("가게명 중복 확인 - 존재하는 경우")
    void existsByName_ExistsTrue() {
        
        boolean exists = storeRepository.existsByName("메인 한식집");

        
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("가게명 중복 확인 - 존재하지 않는 경우")
    void existsByName_ExistsFalse() {
        
        boolean exists = storeRepository.existsByName("존재하지않는가게");

        
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("ID로 삭제되지 않은 가게 조회 - 존재하는 경우")
    void findByIdAndIsDeletedFalse_ExistsSuccess() {
        
        Optional<Store> result = storeRepository.findByIdAndIsDeletedFalse(mainStore.getId());

        
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("메인 한식집");
    }

    @Test
    @DisplayName("ID로 삭제되지 않은 가게 조회 - 존재하지 않는 경우")
    void findByIdAndIsDeletedFalse_NotExists() {
        
        Optional<Store> result = storeRepository.findByIdAndIsDeletedFalse(java.util.UUID.randomUUID());

        
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("가게과 사용자 존재 확인 - 존재하는 경우")
    void existsByStoreAndUser_ExistsTrue() {
        
        boolean exists = storeRepository.existsByStoreAndUser(mainStore.getId(), ownerUser.getId());

        
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("가게과 사용자 존재 확인 - 존재하지 않는 경우")
    void existsByStoreAndUser_ExistsFalse() {
        
        boolean exists = storeRepository.existsByStoreAndUser(mainStore.getId(), java.util.UUID.randomUUID());

        
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("가게과 사용자 존재 확인 - 삭제된 사용자")
    void existsByStoreAndUser_DeletedUser() {
        
        boolean exists = storeRepository.existsByStoreAndUser(mainStore.getId(), deletedUser.getId());

        
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("카테고리별 가게 페이징 조회")
    void findAllByCategoryAndCursor_Success() {
        
        LocalDateTime cursor = LocalDateTime.now().plusMinutes(1);
        Pageable pageable = PageRequest.of(0, 10);

        
        Slice<Store> storeSlice = storeRepository.findAllByCategoryAndCursor(
                koreanCategory.getId(), cursor, pageable);

        
        assertThat(storeSlice.getContent()).hasSize(2); 
        assertThat(storeSlice.hasNext()).isFalse();
    }

    @Test
    @DisplayName("키워드로 가게 검색 - 가게명으로 검색")
    void findAllByKeyWord_SearchByStoreName() {
        
        LocalDateTime cursor = LocalDateTime.now().plusMinutes(1);
        Pageable pageable = PageRequest.of(0, 10);

        
        Slice<Store> storeSlice = storeRepository.findAllByKeyWord("한식", cursor, pageable);

        
        assertThat(storeSlice.getContent()).hasSize(1); 
        assertThat(storeSlice.getContent()).extracting("name").contains("메인 한식집");
    }

    @Test
    @DisplayName("키워드로 가게 검색 - 카테고리명으로 검색")
    void findAllByKeyWord_SearchByCategory() {
        
        
        Store chineseStore = Store.builder()
                .name("중국집")
                .address("서울시 강남구 논현동")
                .phone("02-2222-2222")
                .content("맛있는 중식요리")
                .minPrice(12000)
                .deliveryTip(2500)
                .operationHours("10:00-21:00")
                .closedDays("월요일")
                .user(ownerUser)
                .storeCategory(chineseCategory)
                .region(region)
                .build();
        entityManager.persistAndFlush(chineseStore);

        LocalDateTime cursor = LocalDateTime.now().plusMinutes(1);
        Pageable pageable = PageRequest.of(0, 10);

        
        Slice<Store> storeSlice = storeRepository.findAllByKeyWord("중식", cursor, pageable);

        
        assertThat(storeSlice.getContent()).hasSize(1);
        assertThat(storeSlice.getContent()).extracting("name").contains("중국집");
    }

    @Test
    @DisplayName("키워드로 가게 검색 - 검색 결과 없음")
    void findAllByKeyWord_NoResults() {
        
        LocalDateTime cursor = LocalDateTime.now().plusMinutes(1);
        Pageable pageable = PageRequest.of(0, 10);

        
        Slice<Store> storeSlice = storeRepository.findAllByKeyWord("일식", cursor, pageable);

        
        assertThat(storeSlice.getContent()).isEmpty();
    }

    @Test
    @DisplayName("가게 저장")
    void save_Success() {
        
        Store newStore = Store.builder()
                .name("새로운 가게")
                .address("서울시 강남구 청담동")
                .phone("02-4444-4444")
                .content("새로 오픈한 가게")
                .minPrice(10000)
                .deliveryTip(2000)
                .operationHours("09:00-21:00")
                .closedDays("화요일")
                .user(ownerUser)
                .storeCategory(koreanCategory)
                .region(region)
                .build();

        
        Store savedStore = storeRepository.save(newStore);

        
        assertThat(savedStore.getId()).isNotNull();
        assertThat(savedStore.getName()).isEqualTo("새로운 가게");

        
        Optional<Store> foundStore = storeRepository.findByIdAndIsDeletedFalse(savedStore.getId());
        assertThat(foundStore).isPresent();
        assertThat(foundStore.get().getName()).isEqualTo("새로운 가게");
    }

    @Test
    @DisplayName("가게 삭제")
    void delete_Success() {
        
        long initialCount = storeRepository.count();

        
        storeRepository.delete(mainStore);

        
        long finalCount = storeRepository.count();
        assertThat(finalCount).isEqualTo(initialCount - 1);

        Optional<Store> deletedStore = storeRepository.findByIdAndIsDeletedFalse(mainStore.getId());
        assertThat(deletedStore).isEmpty();
    }
}
