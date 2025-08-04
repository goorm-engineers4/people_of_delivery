package com.example.cloudfour.peopleofdelivery.unit.domain.cart.repository;

import com.example.cloudfour.peopleofdelivery.domain.cart.entity.Cart;
import com.example.cloudfour.peopleofdelivery.domain.cart.repository.CartRepository;
import com.example.cloudfour.peopleofdelivery.domain.region.entity.Region;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.StoreCategory;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.LoginType;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CartRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CartRepository cartRepository;

    private User user;
    private User deletedUser;
    private Store store;
    private Cart cart;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@example.com")
                .nickname("testuser")
                .password("password")
                .number("01012345678")
                .role(Role.CUSTOMER)
                .loginType(LoginType.LOCAL)
                .build();
        entityManager.persist(user);

        deletedUser = User.builder()
                .email("deleted@example.com")
                .nickname("deleteduser")
                .password("password")
                .number("01087654321")
                .role(Role.CUSTOMER)
                .loginType(LoginType.LOCAL)
                .build();
        entityManager.persist(deletedUser);

        Region region = Region.builder()
                .si("서울특별시")
                .gu("강남구")
                .dong("역삼동")
                .build();
        entityManager.persist(region);

        StoreCategory storeCategory = StoreCategory.builder()
                .category("치킨")
                .build();
        entityManager.persist(storeCategory);

        store = Store.builder()
                .user(user)
                .name("테스트 가게")
                .address("상세 주소")
                .phone("010-0000-0000")
                .minPrice(10000)
                .deliveryTip(3000)
                .operationHours("10:00 - 22:00")
                .closedDays("연중무휴")
                .region(region)
                .storeCategory(storeCategory)
                .build();
        entityManager.persist(store);

        cart = Cart.builder().build();
        cart.setUser(user);
        cart.setStore(store);
        entityManager.persist(cart);

        entityManager.flush();
        entityManager.clear();
    }


    @Test
    @DisplayName("findByIdAndUser: 사용자 ID와 장바구니 ID로 장바구니 조회 성공")
    void findByIdAndUser_success() {
        Optional<Cart> foundCart = cartRepository.findByIdAndUser(cart.getId(), user.getId());

        assertThat(foundCart).isPresent();
        assertThat(foundCart.get().getId()).isEqualTo(cart.getId());
        assertThat(foundCart.get().getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("findByIdAndUser: 존재하지 않는 장바구니 ID로 조회 시 실패")
    void findByIdAndUser_fail_wrongCartId() {
        Optional<Cart> foundCart = cartRepository.findByIdAndUser(UUID.randomUUID(), user.getId());

        assertThat(foundCart).isNotPresent();
    }

    @Test
    @DisplayName("findByIdAndUser: 삭제된 사용자로 조회 시 실패")
    void findByIdAndUser_fail_deletedUser() {
        deletedUser.softDelete();
        entityManager.merge(deletedUser);

        Cart deletedUserCart = Cart.builder().build();
        deletedUserCart.setUser(deletedUser);
        deletedUserCart.setStore(store);
        entityManager.persist(deletedUserCart);
        entityManager.flush();

        Optional<Cart> foundCart = cartRepository.findByIdAndUser(deletedUserCart.getId(), deletedUser.getId());

        assertThat(foundCart).isNotPresent();
    }

    @Test
    @DisplayName("existsByUserAndStore: 사용자 ID와 가게 ID로 장바구니 존재 여부 확인 성공")
    void existsByUserAndStore_true() {
        boolean exists = cartRepository.existsByUserAndStore(user.getId(), store.getId());

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByUserAndStore: 존재하지 않는 장바구니 확인 시 false 반환")
    void existsByUserAndStore_false() {
        User newUser = User.builder()
                .email("newuser@example.com")
                .nickname("newuser")
                .password("password")
                .number("01011112222")
                .role(Role.CUSTOMER)
                .loginType(LoginType.LOCAL)
                .build();
        entityManager.persist(newUser);
        entityManager.flush();

        boolean exists = cartRepository.existsByUserAndStore(newUser.getId(), store.getId());

        assertThat(exists).isFalse();
    }


    @Test
    @DisplayName("existsByUserAndStore: 삭제된 사용자로 확인 시 false 반환")
    void existsByUserAndStore_false_deletedUser() {
        deletedUser.softDelete();
        entityManager.merge(deletedUser);

        Cart deletedUserCart = Cart.builder().build();
        deletedUserCart.setUser(deletedUser);
        deletedUserCart.setStore(store);
        entityManager.persist(deletedUserCart);
        entityManager.flush();

        boolean exists = cartRepository.existsByUserAndStore(deletedUser.getId(), store.getId());

        assertThat(exists).isFalse();
    }
}