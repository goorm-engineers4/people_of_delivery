package com.example.cloudfour.peopleofdelivery.domain.menu.entity;

import com.example.cloudfour.peopleofdelivery.domain.cartitem.entity.CartItem;
import com.example.cloudfour.peopleofdelivery.domain.order.entity.OrderItem;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_menuoption")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)  // Builder만 사용하도록 제한
public class MenuOption {
    @Id
    @UuidGenerator
    @Column(name = "menuOptionId")
    private UUID id;  // 자동 생성되는 UUID

    @Column(name = "optionName", nullable = false)
    private String optionName;

    @Column(name = "additionalPrice", nullable = false)
    private Integer additionalPrice;  // INT 타입으로 변경

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menuId", nullable = false)
    private Menu menu;

    @OneToMany(mappedBy = "menuOption", cascade = CascadeType.ALL)
    @Builder.Default
    private List<CartItem> cartItems = new ArrayList<>();

    @OneToMany(mappedBy = "menuOption", cascade = CascadeType.ALL)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    public static class MenuOptionBuilder{
        private MenuOptionBuilder id(UUID id){
            throw new UnsupportedOperationException("id 수정 불가");
        }
    }

    public void setMenu(Menu menu){
        this.menu = menu;
        menu.getMenuOptions().add(this);
    }
}