package com.example.cloudfour.peopleofdelivery.domain.order.entity;

import com.example.cloudfour.peopleofdelivery.domain.order.enums.OrderStatus;
import com.example.cloudfour.peopleofdelivery.domain.order.enums.OrderType;
import com.example.cloudfour.peopleofdelivery.domain.order.enums.ReceiptType;
import com.example.cloudfour.peopleofdelivery.domain.order.exception.OrderErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.order.exception.OrderException;
import com.example.cloudfour.peopleofdelivery.domain.payment.entity.Payment;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.global.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "p_order")
public class Order extends BaseEntity {
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

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    public static class OrderBuilder{
        private OrderBuilder id(UUID id) {
            throw new OrderException(OrderErrorCode.CREATE_FAILED);
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

    public void updateOrderStatus(OrderStatus orderStatus){
        this.status = orderStatus;
    }
}
