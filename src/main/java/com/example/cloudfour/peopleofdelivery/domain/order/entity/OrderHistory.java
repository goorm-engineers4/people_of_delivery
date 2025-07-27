package com.example.cloudfour.peopleofdelivery.domain.order.entity;

import com.example.cloudfour.peopleofdelivery.domain.order.enums.OrderStatus;
import com.example.cloudfour.peopleofdelivery.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "p_order_history")
public class OrderHistory extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId" ,nullable = false)
    private Order order;

    public static class OrderHistoryBuilder{
        private OrderHistoryBuilder id(UUID id){
            throw new UnsupportedOperationException("id 수동 생성 불가");
        }
    }

    public void setOrder(Order order){
        this.order = order;
        order.setOrderHistory(this);
    }
}
