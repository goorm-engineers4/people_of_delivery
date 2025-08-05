package com.example.cloudfour.peopleofdelivery.unit.domain.order.repository;

import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuCategory;
import com.example.cloudfour.peopleofdelivery.domain.order.entity.Order;
import com.example.cloudfour.peopleofdelivery.domain.order.enums.OrderStatus;
import com.example.cloudfour.peopleofdelivery.domain.order.enums.OrderType;
import com.example.cloudfour.peopleofdelivery.domain.order.enums.ReceiptType;
import com.example.cloudfour.peopleofdelivery.domain.order.repository.OrderRepository;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@DisplayName("주문 레포지토리 테스트")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User customer;
    private User owner;
    private User deletedUser;
    private Store store;
    private Store anotherStore;
    private Order order1;
    private Order order2;
    private Order deletedOrder;
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
                .nickname("삭제된 고객")
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

        anotherStore = Store.builder()
                .name("다른 매장")
                .address("서울시 강남구 삼성동")
                .phone("02-5678-9012")
                .content("또 다른 음식점")
                .minPrice(20000)
                .deliveryTip(4000)
                .operationHours("10:00-23:00")
                .closedDays("월요일")
                .user(owner)
                .storeCategory(storeCategory)
                .region(region)
                .build();
        entityManager.persistAndFlush(anotherStore);

        order1 = Order.builder()
                .orderType(OrderType.온라인)
                .receiptType(ReceiptType.배달)
                .address("서울시 강남구 역삼동 123")
                .request("빠른 배송 부탁드립니다")
                .totalPrice(25000)
                .status(OrderStatus.주문접수)
                .user(customer)
                .store(store)
                .build();
        entityManager.persistAndFlush(order1);

        order2 = Order.builder()
                .orderType(OrderType.대면)
                .receiptType(ReceiptType.포장)
                .address("서울시 강남구 역삼동 456")
                .request("포장 주문입니다")
                .totalPrice(18000)
                .status(OrderStatus.배달중)
                .user(customer)
                .store(anotherStore)
                .build();
        entityManager.persistAndFlush(order2);

        deletedOrder = Order.builder()
                .orderType(OrderType.온라인)
                .receiptType(ReceiptType.배달)
                .address("서울시 강남구 삼성동 789")
                .request("삭제된 주문")
                .totalPrice(30000)
                .status(OrderStatus.주문접수)
                .user(deletedUser)
                .store(store)
                .build();
        entityManager.persistAndFlush(deletedOrder);

        entityManager.clear();
    }

    @Test
    @DisplayName("주문 ID로 삭제되지 않은 주문 조회 - 존재하는 경우")
    void findById_ExistsSuccess() {
        Optional<Order> result = orderRepository.findById(order1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(order1.getId());
        assertThat(result.get().getTotalPrice()).isEqualTo(25000);
        assertThat(result.get().getStatus()).isEqualTo(OrderStatus.주문접수);
    }

    @Test
    @DisplayName("주문 ID로 삭제되지 않은 주문 조회 - 존재하지 않는 경우")
    void findById_NotExists() {
        Optional<Order> result = orderRepository.findById(UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("사용자 ID로 주문 목록 조회 - 정상 케이스")
    void findAllByUserId_Success() {
        LocalDateTime cursor = LocalDateTime.now().plusDays(1);
        Pageable pageable = PageRequest.of(0, 10);

        Slice<Order> result = orderRepository.findAllByUserId(customer.getId(), cursor, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting("totalPrice").containsExactly(18000, 25000);
        assertThat(result.hasNext()).isFalse();
    }

    @Test
    @DisplayName("사용자 ID로 주문 목록 조회 - 삭제된 사용자")
    void findAllByUserId_DeletedUser() {
        LocalDateTime cursor = LocalDateTime.now().plusDays(1);
        Pageable pageable = PageRequest.of(0, 10);

        Slice<Order> result = orderRepository.findAllByUserId(deletedUser.getId(), cursor, pageable);

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("사용자 ID로 주문 목록 조회 - 커서 페이징")
    void findAllByUserId_CursorPaging() {
        LocalDateTime cursor = order2.getCreatedAt();
        Pageable pageable = PageRequest.of(0, 1);

        Slice<Order> result = orderRepository.findAllByUserId(customer.getId(), cursor, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(order1.getId());
        assertThat(result.hasNext()).isFalse();
    }

    @Test
    @DisplayName("매장 ID로 주문 목록 조회 - 정상 케이스")
    void findAllByStoreId_Success() {
        LocalDateTime cursor = LocalDateTime.now().plusDays(1);
        Pageable pageable = PageRequest.of(0, 10);

        Slice<Order> result = orderRepository.findAllByStoreId(store.getId(), cursor, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(order1.getId());
        assertThat(result.hasNext()).isFalse();
    }

    @Test
    @DisplayName("매장 ID로 주문 목록 조회 - 매장에 주문이 없는 경우")
    void findAllByStoreId_NoOrders() {
        Store emptyStore = Store.builder()
                .name("빈 매장")
                .address("서울시 강남구 논현동")
                .phone("02-9999-9999")
                .content("주문이 없는 매장")
                .minPrice(10000)
                .deliveryTip(2000)
                .operationHours("09:00-21:00")
                .closedDays("일요일")
                .user(owner)
                .storeCategory(storeCategory)
                .region(region)
                .build();
        entityManager.persistAndFlush(emptyStore);

        LocalDateTime cursor = LocalDateTime.now().plusDays(1);
        Pageable pageable = PageRequest.of(0, 10);

        Slice<Order> result = orderRepository.findAllByStoreId(emptyStore.getId(), cursor, pageable);

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("주문 ID와 사용자 ID로 존재 여부 확인 - 존재하는 경우")
    void existsByOrderIdAndUserId_ExistsTrue() {
        boolean exists = orderRepository.existsByOrderIdAndUserId(order1.getId(), customer.getId());

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("주문 ID와 사용자 ID로 존재 여부 확인 - 존재하지 않는 경우")
    void existsByOrderIdAndUserId_ExistsFalse() {
        boolean exists = orderRepository.existsByOrderIdAndUserId(order1.getId(), owner.getId());

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("주문 ID와 사용자 ID로 존재 여부 확인 - 삭제된 사용자")
    void existsByOrderIdAndUserId_DeletedUser() {
        boolean exists = orderRepository.existsByOrderIdAndUserId(deletedOrder.getId(), deletedUser.getId());

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("주문 저장")
    void save_Success() {
        Order newOrder = Order.builder()
                .orderType(OrderType.대면)
                .receiptType(ReceiptType.포장)
                .address("서울시 서초구 반포동")
                .request("새로운 주문")
                .totalPrice(22000)
                .status(OrderStatus.주문접수)
                .user(customer)
                .store(store)
                .build();

        Order savedOrder = orderRepository.save(newOrder);

        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getTotalPrice()).isEqualTo(22000);
        assertThat(savedOrder.getRequest()).isEqualTo("새로운 주문");

        Optional<Order> foundOrder = orderRepository.findById(savedOrder.getId());
        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getTotalPrice()).isEqualTo(22000);
    }

    @Test
    @DisplayName("주문 상태 업데이트")
    void updateOrderStatus_Success() {
        Order orderToUpdate = orderRepository.findById(order1.getId()).orElseThrow();
        OrderStatus newStatus = OrderStatus.배달완료;

        orderToUpdate.updateOrderStatus(newStatus);
        orderRepository.save(orderToUpdate);

        Order updatedOrder = orderRepository.findById(order1.getId()).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.배달완료);
    }

    @Test
    @DisplayName("주문 소프트 삭제")
    void softDelete_Success() {
        Order orderToDelete = orderRepository.findById(order1.getId()).orElseThrow();

        orderToDelete.softDelete();
        orderRepository.save(orderToDelete);

        Optional<Order> deletedOrder = orderRepository.findById(order1.getId());
        assertThat(deletedOrder).isEmpty();
    }

    @Test
    @DisplayName("시간 순서대로 정렬 확인")
    void findAllByUserId_OrderByCreatedAtDesc() {
        Order recentOrder = Order.builder()
                .orderType(OrderType.온라인)
                .receiptType(ReceiptType.배달)
                .address("서울시 강남구 청담동")
                .request("최근 주문")
                .totalPrice(35000)
                .status(OrderStatus.주문접수)
                .user(customer)
                .store(store)
                .build();
        entityManager.persistAndFlush(recentOrder);

        LocalDateTime cursor = LocalDateTime.now().plusDays(1);
        Pageable pageable = PageRequest.of(0, 10);

        Slice<Order> result = orderRepository.findAllByUserId(customer.getId(), cursor, pageable);

        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().get(0).getId()).isEqualTo(recentOrder.getId());
        assertThat(result.getContent().get(0).getCreatedAt())
                .isAfter(result.getContent().get(1).getCreatedAt());
    }
}
