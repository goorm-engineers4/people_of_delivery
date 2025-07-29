package com.example.cloudfour.peopleofdelivery.domain.order.entity;

import com.example.cloudfour.peopleofdelivery.domain.order.enums.OrderStatus;
import com.example.cloudfour.peopleofdelivery.domain.order.enums.OrderType;
import com.example.cloudfour.peopleofdelivery.domain.order.enums.ReceiptType;
import com.example.cloudfour.peopleofdelivery.domain.payment.entity.Payment;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "p_order")
public class Order{
    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReceiptType receiptType;

    @Column(nullable = false)
    private String address;

    private String request;

    @Column(nullable = false)
    private Integer totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeId", nullable = false)
    private Store store;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Payment payment;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private OrderHistory orderHistory;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    public static class OrderBuilder{
        private OrderBuilder id(UUID id){
            throw new UnsupportedOperationException("id 수동 생성 불가");
        }
    }

    public void setUser(User user){
        this.user = user;
        user.getOrders().add(this);
    }

    public void setStore(Store store){
        this.store = store;
        store.getOrders().add(this);
    }

    public void setPayment(Payment payment){
        this.payment = payment;
    }

    public void setOrderHistory(OrderHistory orderHistory){
        this.orderHistory = orderHistory;
    }
}
