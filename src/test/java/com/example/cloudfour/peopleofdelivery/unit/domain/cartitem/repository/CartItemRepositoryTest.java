package com.example.cloudfour.peopleofdelivery.unit.domain.cartitem.repository;

import com.example.cloudfour.peopleofdelivery.domain.cart.entity.Cart;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.entity.CartItem;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.repository.CartItemRepository;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.Menu;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuCategory;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuOption;
import com.example.cloudfour.peopleofdelivery.domain.menu.enums.MenuStatus;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CartItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CartItemRepository cartItemRepository;

    private User user;
    private Store store;
    private Cart cart;
    private Menu menu;
    private MenuOption menuOption;
    private CartItem cartItem;
    private MenuCategory menuCategory;

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

        menuCategory = MenuCategory.builder()
                .category("치킨")
                .build();
        entityManager.persist(menuCategory);

        menu = Menu.builder()
                .store(store)
                .menuCategory(menuCategory)
                .name("후라이드치킨")
                .price(18000)
                .content("대표 메뉴 설명입니다.")
                .status(MenuStatus.판매중)
                .build();
        entityManager.persist(menu);

        menuOption = MenuOption.builder()
                .menu(menu)
                .optionName("뼈/순살")
                .additionalPrice(0)
                .build();
        entityManager.persist(menuOption);

        cart = Cart.builder().build();
        cart.setUser(user);
        cart.setStore(store);
        entityManager.persist(cart);

        cartItem = CartItem.builder()
                .cart(cart)
                .menu(menu)
                .menuOption(menuOption)
                .quantity(2)
                .price(18000)
                .build();
        entityManager.persist(cartItem);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("existsByCartItemAndUser: 장바구니 아이템 ID와 사용자 ID로 존재 확인 - true")
    void existsByCartItemAndUser_true() {
        boolean exists = cartItemRepository.existsByCartItemAndUser(cartItem.getId(), user.getId());
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByCartItemAndUser: 잘못된 사용자 ID로 확인 - false")
    void existsByCartItemAndUser_false_wrongUser() {
        User newUser = User.builder()
                .email("nouser@example.com")
                .nickname("nouser")
                .password("password")
                .number("01055556666")
                .role(Role.CUSTOMER)
                .loginType(LoginType.LOCAL)
                .build();
        entityManager.persist(newUser);
        entityManager.flush();

        boolean exists = cartItemRepository.existsByCartItemAndUser(cartItem.getId(), newUser.getId());
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("findAllByCartId: 장바구니 ID와 사용자 ID로 장바구니 아이템 리스트 조회 성공")
    void findAllByCartId_success() {
        List<CartItem> items = cartItemRepository.findAllByCartId(cart.getId(), user.getId());
        assertThat(items).isNotEmpty();
        assertThat(items.get(0).getId()).isEqualTo(cartItem.getId());
        assertThat(items.get(0).getCart().getId()).isEqualTo(cart.getId());
    }

    @Test
    @DisplayName("findAllByCartId: 잘못된 사용자 ID로 조회 - 빈 리스트")
    void findAllByCartId_fail_wrongUser() {
        User anotherUser = User.builder()
                .email("nouser2@example.com")
                .nickname("nouser2")
                .password("password")
                .number("01099998888")
                .role(Role.CUSTOMER)
                .loginType(LoginType.LOCAL)
                .build();
        entityManager.persist(anotherUser);
        entityManager.flush();

        List<CartItem> items = cartItemRepository.findAllByCartId(cart.getId(), anotherUser.getId());
        assertThat(items).isEmpty();
    }

    @Test
    @DisplayName("findAllByCartId: 삭제된 유저로 조회 - 빈 리스트")
    void findAllByCartId_fail_deletedUser() {
        user.softDelete();
        entityManager.merge(user);
        entityManager.flush();

        List<CartItem> items = cartItemRepository.findAllByCartId(cart.getId(), user.getId());
        assertThat(items).isEmpty();
    }
}
