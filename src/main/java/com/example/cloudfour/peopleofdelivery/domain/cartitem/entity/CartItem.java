package com.example.cloudfour.peopleofdelivery.domain.cartitem.entity;

import com.example.cloudfour.peopleofdelivery.domain.cart.entity.Cart;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CartItem {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Cart cart;

    //TODO 메뉴, 메뉴 옵션 ManytoOne

}
