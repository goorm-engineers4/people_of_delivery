package com.example.cloudfour.peopleofdelivery.domain.orderitem.entity;

import com.example.cloudfour.peopleofdelivery.domain.order.entity.Order;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    private Integer quantity;

    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Order order;

    //TODO: 메뉴, 메뉴옵션 ManyToOne 추가
}
