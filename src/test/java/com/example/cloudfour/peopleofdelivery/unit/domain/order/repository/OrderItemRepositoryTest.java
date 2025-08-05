package com.example.cloudfour.peopleofdelivery.unit.domain.order.repository;

import com.example.cloudfour.peopleofdelivery.domain.menu.entity.Menu;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuCategory;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuOption;
import com.example.cloudfour.peopleofdelivery.domain.menu.enums.MenuStatus;
import com.example.cloudfour.peopleofdelivery.domain.order.entity.Order;
import com.example.cloudfour.peopleofdelivery.domain.order.entity.OrderItem;
import com.example.cloudfour.peopleofdelivery.domain.order.enums.OrderStatus;
import com.example.cloudfour.peopleofdelivery.domain.order.enums.OrderType;
import com.example.cloudfour.peopleofdelivery.domain.order.enums.ReceiptType;
import com.example.cloudfour.peopleofdelivery.domain.order.repository.OrderItemRepository;
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
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@DisplayName("주문 아이템 레포지토리 테스트")
class OrderItemRepositoryTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User customer;
    private User owner;
    private User deletedUser;
    private Store store;
    private Menu menu1;
    private Menu menu2;
    private MenuOption option1;
    private MenuOption option2;
    private Order order;
    private Order deletedOrder;
    private OrderItem orderItem1;
    private OrderItem orderItem2;
    private OrderItem deletedOrderItem;
    private Region region;
    private StoreCategory storeCategory;
    private MenuCategory menuCategory;

    @BeforeEach
    void setUp() {
        region = Region.builder()
                .si("서울특별시")
                .gu("강남구")
                .dong("역삼동")
                .build();
        entityManager.persistAndFlush(region);

        storeCategory = StoreCategory.builder()
                .category("한식")
                .build();
        entityManager.persistAndFlush(storeCategory);

        menuCategory = MenuCategory.builder()
                .category("메인메뉴")
                .build();
        entityManager.persistAndFlush(menuCategory);

        customer = User.builder()
                .email("customer@example.com")
                .nickname("고객")
                .number("010-1111-1111")
                .password("password")
                .role(Role.CUSTOMER)
                .loginType(LoginType.LOCAL)
                .emailVerified(true)
                .build();
        entityManager.persistAndFlush(customer);

        owner = User.builder()
                .email("owner@example.com")
                .nickname("사장")
                .number("010-2222-2222")
                .password("password")
                .role(Role.OWNER)
                .loginType(LoginType.LOCAL)
                .emailVerified(true)
                .build();
        entityManager.persistAndFlush(owner);

        deletedUser = User.builder()
                .email("deleted@example.com")
                .nickname("삭제된고객")
                .number("010-9999-9999")
                .password("password")
                .role(Role.CUSTOMER)
                .loginType(LoginType.LOCAL)
                .emailVerified(true)
                .build();
        entityManager.persistAndFlush(deletedUser);

        entityManager.getEntityManager()
                .createQuery("UPDATE User u SET u.isDeleted = true WHERE u.id = :id")
                .setParameter("id", deletedUser.getId())
                .executeUpdate();
        entityManager.flush();

        store = Store.builder()
                .name("테스트 매장")
                .address("서울시 강남구 역삼동")
                .phone("02-1234-5678")
                .content("맛있는 음식점")
                .minPrice(15000)
                .deliveryTip(3000)
                .operationHours("11:00-22:00")
                .closedDays("연중무휴")
                .user(owner)
                .storeCategory(storeCategory)
                .region(region)
                .build();
        entityManager.persistAndFlush(store);

        menu1 = Menu.builder()
                .store(store)
                .menuCategory(menuCategory)
                .name("치킨버거")
                .price(12000)
                .content("맛있는 치킨버거")
                .status(MenuStatus.판매중)
                .build();
        entityManager.persistAndFlush(menu1);

        menu2 = Menu.builder()
                .store(store)
                .menuCategory(menuCategory)
                .name("불고기버거")
                .price(14000)
                .content("맛있는 불고기버거")
                .status(MenuStatus.판매중)
                .build();
        entityManager.persistAndFlush(menu2);

        option1 = MenuOption.builder()
                .optionName("치즈 추가")
                .additionalPrice(1000)
                .menu(menu1)
                .build();
        entityManager.persistAndFlush(option1);

        option2 = MenuOption.builder()
                .optionName("감자튀김 세트")
                .additionalPrice(2000)
                .menu(menu2)
                .build();
        entityManager.persistAndFlush(option2);

        order = Order.builder()
                .orderType(OrderType.온라인)
                .receiptType(ReceiptType.배달)
                .address("서울시 강남구 역삼동 123")
                .request("빠른 배송 부탁드립니다")
                .totalPrice(29000)
                .status(OrderStatus.주문접수)
                .user(customer)
                .store(store)
                .build();
        entityManager.persistAndFlush(order);

        deletedOrder = Order.builder()
                .orderType(OrderType.온라인)
                .receiptType(ReceiptType.배달)
                .address("서울시 강남구 삼성동 789")
                .request("삭제된 주문")
                .totalPrice(15000)
                .status(OrderStatus.주문접수)
                .user(deletedUser)
                .store(store)
                .build();
        entityManager.persistAndFlush(deletedOrder);

        entityManager.getEntityManager()
                .createQuery("UPDATE Order o SET o.isDeleted = true WHERE o.id = :id")
                .setParameter("id", deletedOrder.getId())
                .executeUpdate();
        entityManager.flush();

        orderItem1 = OrderItem.builder()
                .quantity(1)
                .price(13000)
                .order(order)
                .menu(menu1)
                .menuOption(option1)
                .build();
        entityManager.persistAndFlush(orderItem1);

        orderItem2 = OrderItem.builder()
                .quantity(1)
                .price(16000)
                .order(order)
                .menu(menu2)
                .menuOption(option2)
                .build();
        entityManager.persistAndFlush(orderItem2);

        deletedOrderItem = OrderItem.builder()
                .quantity(1)
                .price(15000)
                .order(deletedOrder)
                .menu(menu1)
                .menuOption(option1)
                .build();
        entityManager.persistAndFlush(deletedOrderItem);

        entityManager.clear();
    }

    @Test
    @DisplayName("주문 ID로 주문 아이템 조회 - 정상 케이스")
    void findByOrderId_Success() {
        List<OrderItem> result = orderItemRepository.findByOrderId(order.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting("price").containsExactlyInAnyOrder(13000, 16000);
        assertThat(result).extracting("quantity").containsExactlyInAnyOrder(1, 1);
    }

    @Test
    @DisplayName("주문 ID로 주문 아이템 조회 - 존재하지 않는 주문")
    void findByOrderId_NotExists() {
        List<OrderItem> result = orderItemRepository.findByOrderId(UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("주문 ID로 주문 아이템 조회 - 삭제된 주문")
    void findByOrderId_DeletedOrder() {
        List<OrderItem> result = orderItemRepository.findByOrderId(deletedOrder.getId());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("주문 아이템 저장")
    void save_Success() {
        Menu newMenu = Menu.builder()
                .store(store)
                .menuCategory(menuCategory)
                .name("새로운 메뉴")
                .price(20000)
                .content("새로운 메뉴입니다")
                .status(MenuStatus.판매중)
                .build();
        entityManager.persistAndFlush(newMenu);

        MenuOption newOption = MenuOption.builder()
                .optionName("새로운 옵션")
                .additionalPrice(3000)
                .menu(newMenu)
                .build();
        entityManager.persistAndFlush(newOption);

        OrderItem newOrderItem = OrderItem.builder()
                .quantity(2)
                .price(46000)
                .order(order)
                .menu(newMenu)
                .menuOption(newOption)
                .build();

        OrderItem savedOrderItem = orderItemRepository.save(newOrderItem);

        assertThat(savedOrderItem.getId()).isNotNull();
        assertThat(savedOrderItem.getQuantity()).isEqualTo(2);
        assertThat(savedOrderItem.getPrice()).isEqualTo(46000);

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
        assertThat(orderItems).hasSize(3);
        assertThat(orderItems).extracting("price").contains(46000);
    }

    @Test
    @DisplayName("주문 아이템 삭제")
    void delete_Success() {
        long initialCount = orderItemRepository.count();

        orderItemRepository.delete(orderItem1);

        long finalCount = orderItemRepository.count();
        assertThat(finalCount).isEqualTo(initialCount - 1);

        List<OrderItem> remainingItems = orderItemRepository.findByOrderId(order.getId());
        assertThat(remainingItems).hasSize(1);
        assertThat(remainingItems.get(0).getId()).isEqualTo(orderItem2.getId());
    }

    @Test
    @DisplayName("메뉴별 주문 아이템 조회")
    void findByMenuId_Success() {
        List<OrderItem> result = orderItemRepository.findAll().stream()
                .filter(item -> item.getMenu().getId().equals(menu1.getId()))
                .filter(item -> !item.getOrder().getIsDeleted())
                .toList();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMenu().getName()).isEqualTo("치킨버거");
        assertThat(result.get(0).getPrice()).isEqualTo(13000);
    }

    @Test
    @DisplayName("주문 아이템과 연관 엔티티 로딩 테스트")
    void findByOrderId_WithRelatedEntities() {
        List<OrderItem> result = orderItemRepository.findByOrderId(order.getId());

        assertThat(result).hasSize(2);

        OrderItem item1 = result.stream()
                .filter(item -> item.getMenu().getName().equals("치킨버거"))
                .findFirst()
                .orElseThrow();

        assertThat(item1.getMenu().getName()).isEqualTo("치킨버거");
        assertThat(item1.getMenuOption().getOptionName()).isEqualTo("치즈 추가");
        assertThat(item1.getOrder().getId()).isEqualTo(order.getId());
    }

    @Test
    @DisplayName("주문 아이템 ID로 조회")
    void findById_Success() {
        OrderItem result = orderItemRepository.findById(orderItem1.getId()).orElseThrow();

        assertThat(result.getId()).isEqualTo(orderItem1.getId());
        assertThat(result.getQuantity()).isEqualTo(1);
        assertThat(result.getPrice()).isEqualTo(13000);
        assertThat(result.getMenu().getName()).isEqualTo("치킨버거");
        assertThat(result.getMenuOption().getOptionName()).isEqualTo("치즈 추가");
    }

    @Test
    @DisplayName("주문 아이템 업데이트")
    void update_Success() {
        OrderItem itemToUpdate = orderItemRepository.findById(orderItem1.getId()).orElseThrow();

        entityManager.getEntityManager()
                .createQuery("UPDATE OrderItem oi SET oi.quantity = :quantity, oi.price = :price WHERE oi.id = :id")
                .setParameter("quantity", 2)
                .setParameter("price", 26000)
                .setParameter("id", itemToUpdate.getId())
                .executeUpdate();
        entityManager.flush();
        entityManager.clear();

        OrderItem updatedItem = orderItemRepository.findById(orderItem1.getId()).orElseThrow();
        assertThat(updatedItem.getQuantity()).isEqualTo(2);
        assertThat(updatedItem.getPrice()).isEqualTo(26000);
    }

    @Test
    @DisplayName("모든 주문 아이템 조회")
    void findAll_Success() {
        List<OrderItem> result = orderItemRepository.findAll();

        assertThat(result).hasSize(3);
        assertThat(result).extracting("price").containsExactlyInAnyOrder(13000, 16000, 15000);
    }

    @Test
    @DisplayName("주문 아이템 존재 여부 확인")
    void existsById_Success() {
        boolean exists = orderItemRepository.existsById(orderItem1.getId());
        boolean notExists = orderItemRepository.existsById(UUID.randomUUID());

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}
