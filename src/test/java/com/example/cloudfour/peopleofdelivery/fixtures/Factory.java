package com.example.cloudfour.peopleofdelivery.fixtures;

import com.example.cloudfour.peopleofdelivery.domain.cart.entity.Cart;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.entity.CartItem;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.Menu;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuCategory;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuOption;
import com.example.cloudfour.peopleofdelivery.domain.menu.enums.MenuStatus;
import com.example.cloudfour.peopleofdelivery.domain.order.entity.Order;
import com.example.cloudfour.peopleofdelivery.domain.order.enums.OrderStatus;
import com.example.cloudfour.peopleofdelivery.domain.order.entity.OrderItem;
import com.example.cloudfour.peopleofdelivery.domain.payment.entity.Payment;
import com.example.cloudfour.peopleofdelivery.domain.payment.entity.PaymentHistory;
import com.example.cloudfour.peopleofdelivery.domain.payment.enums.PaymentStatus;
import com.example.cloudfour.peopleofdelivery.domain.region.entity.Region;
import com.example.cloudfour.peopleofdelivery.domain.review.entity.Review;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.StoreCategory;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.UserAddress;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.LoginType;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.Role;
import com.example.cloudfour.peopleofdelivery.global.ai.entity.AiLog;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Factory {

    public static Region createMockRegion() {
        return Region.builder()
                .si("서울특별시")
                .gu("종로구")
                .dong("청운동")
                .build();
    }

    public static StoreCategory createMockStoreCategory() {
        return StoreCategory.builder()
                .category("치킨")
                .build();
    }

    public static MenuCategory createMockMenuCategory() {
        return MenuCategory.builder()
                .category("치킨류")
                .build();
    }

    public static User createMockUserWithAll() {
        Region region = createMockRegion();

        User user = User.builder()
                .email("test@example.com")
                .nickname("mockUser")
                .password("encoded")
                .number("010-1111-2222")
                .role(Role.CUSTOMER)
                .providerId("google-oauth2|1234567890")
                .loginType(LoginType.GOOGLE)
                .build();

        UserAddress address = UserAddress.builder()
                .address("서울 종로구 청운동 100-1")
                .user(user)
                .region(region)
                .build();

        user.getAddresses().add(address);
        region.getAddresses().add(address);

        StoreCategory storeCategory = createMockStoreCategory();

        Store store = Store.builder()
                .name("맛있는치킨")
                .address("서울시 종로구 청운동 200")
                .phone("02-111-2222")
                .content("바삭하고 맛있는 치킨")
                .minPrice(15000)
                .deliveryTip(2000)
                .operationHours("10:00 - 22:00")
                .closedDays("매주 월요일")
                .rating(4.9f)
                .likeCount(50)
                .reviewCount(10)
                .storePicture("store.jpg")
                .storeCategory(storeCategory)
                .user(user)
                .region(region)
                .build();

        user.getStores().add(store);
        region.getStores().add(store);
        storeCategory.getStores().add(store);

        MenuCategory menuCategory = createMockMenuCategory();

        Menu menu = Menu.builder()
                .store(store)
                .menuCategory(menuCategory)
                .name("후라이드치킨")
                .price(18000)
                .content("기본 치킨")
                .status(MenuStatus.판매중)
                .build();

        menuCategory.getMenus().add(menu);
        store.getMenus().add(menu);

        MenuOption option = MenuOption.builder()
                .optionName("양념소스")
                .additionalPrice(1000)
                .menu(menu)
                .build();

        menu.getMenuOptions().add(option);

        Cart cart = Cart.builder()
                .user(user)
                .store(store)
                .build();

        cart.setUser(user);
        cart.setStore(store);

        CartItem cartItem = CartItem.builder()
                .quantity(1)
                .price(menu.getPrice() + option.getAdditionalPrice())
                .cart(cart)
                .menu(menu)
                .menuOption(option)
                .build();

        cart.getCartItems().add(cartItem);
        menu.getCartItems().add(cartItem);

        Order order = Order.builder()
                .user(user)
                .store(store)
                .status(OrderStatus.주문접수)
                .totalPrice(menu.getPrice() + option.getAdditionalPrice())
                .build();

        user.getOrders().add(order);
        store.getOrders().add(order);

        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .menu(menu)
                .menuOption(option)
                .quantity(1)
                .price(menu.getPrice() + option.getAdditionalPrice())
                .build();

        order.getOrderItems().add(orderItem);
        menu.getOrderItems().add(orderItem);


        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod("card")
                .paymentStatus(PaymentStatus.PAID)
                .totalPrice(menu.getPrice() + option.getAdditionalPrice())
                .paymentKey(123456)
                .build();

        order.setPayment(payment);

        PaymentHistory paymentHistory = PaymentHistory.builder()
                .paymentStatus(PaymentStatus.PAID)
                .payment(payment)
                .build();

        payment.setPaymentHistory(paymentHistory);

        Review review = Review.builder()
                .score(5)
                .content("최고의 맛!")
                .user(user)
                .store(store)
                .build();

        user.getReviews().add(review);
        store.getReviews().add(review);

        AiLog aiLog = AiLog.builder()
                .question("치킨 추천해줘")
                .result("후라이드치킨")
                .build();

        return user;
    }



    public static User createMockUserWithRole(Role role) {
        Region region = createMockRegion();

        User user = User.builder()
                .email("test@example.com")
                .nickname("mockUser")
                .password("encoded")
                .number("010-1111-2222")
                .role(role)
                .providerId("google-oauth2|1234567890")
                .loginType(LoginType.GOOGLE)
                .build();

        try {
            Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, UUID.randomUUID());
        } catch (Exception e) {
            return createMockUserWithRoleAndId(role, UUID.randomUUID());
        }

        UserAddress address = UserAddress.builder()
                .address("서울 종로구 청운동 100-1")
                .user(user)
                .region(region)
                .build();

        user.getAddresses().add(address);
        region.getAddresses().add(address);

        return user;
    }

    public static User createMockUserWithRoleAndRegion(Role role, Region region) {
        User user = User.builder()
                .email("test@example.com")
                .nickname("mockUser")
                .password("encoded")
                .number("010-1111-2222")
                .role(role)
                .providerId("google-oauth2|1234567890")
                .loginType(LoginType.GOOGLE)
                .build();

        UserAddress address = UserAddress.builder()
                .address("서울 종로구 청운동 100-1")
                .user(user)
                .region(region)
                .build();

        user.getAddresses().add(address);
        region.getAddresses().add(address);

        return user;
    }

    public static User createMockUserWithRoleAndId(Role role, UUID id) {
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(id);
        when(mockUser.getRole()).thenReturn(role);
        when(mockUser.getEmail()).thenReturn("test@example.com");
        when(mockUser.getNickname()).thenReturn("mockUser");
        when(mockUser.getNumber()).thenReturn("010-1111-2222");
        when(mockUser.getProviderId()).thenReturn("google-oauth2|1234567890");
        when(mockUser.getLoginType()).thenReturn(LoginType.GOOGLE);
        return mockUser;
    }

    public static User createMockUserWithId(UUID id) {
        return createMockUserWithRoleAndId(Role.CUSTOMER, id);
    }

    public static User createMockUserWithRole(Role role, Region region) {
        User user = User.builder()
                .email("test@example.com")
                .nickname("mockUser")
                .password("encoded")
                .number("010-1111-2222")
                .role(role)
                .providerId("google-oauth2|1234567890")
                .loginType(LoginType.GOOGLE)
                .build();

        UserAddress address = UserAddress.builder()
                .address("서울 종로구 청운동 100-1")
                .user(user)
                .region(region)
                .build();

        user.getAddresses().add(address);
        region.getAddresses().add(address);

        return user;
    }
}
