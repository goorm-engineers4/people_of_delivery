package com.example.cloudfour.peopleofdelivery.domain.cart.entity;

import com.example.cloudfour.peopleofdelivery.domain.cartitem.entity.CartItem;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(nullable = false)
    private Integer count;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    // TODO 가게 ManyToOne으로 추가하기

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "cart",cascade = CascadeType.ALL)
    @Builder.Default
    private List<CartItem> cartItems = new ArrayList<>();
}
