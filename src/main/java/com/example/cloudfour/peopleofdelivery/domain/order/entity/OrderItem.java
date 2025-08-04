package com.example.cloudfour.peopleofdelivery.domain.order.entity;

import com.example.cloudfour.peopleofdelivery.domain.menu.entity.Menu;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuOption;
import com.example.cloudfour.peopleofdelivery.domain.order.exception.OrderItemErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.order.exception.OrderItemException;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "p_orderItem")
public class OrderItem {
    @Id
    @GeneratedValue
    private UUID id;

    private Integer quantity;

    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menuId", nullable = false)
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menuOptionId", nullable = true)
    private MenuOption menuOption;

    public static class OrderItemBuilder{
        private OrderItemBuilder id(UUID id){
            throw new OrderItemException(OrderItemErrorCode.CREATE_FAILED);
        }
    }

    public void setOrder(Order order){
        this.order = order;
        order.getOrderItems().add(this);
    }

    public void setMenu(Menu menu){
        this.menu = menu;
        menu.getOrderItems().add(this);
    }

    public void setMenuOption(MenuOption menuOption){
        if (menuOption != null) {
            this.menuOption = menuOption;
            menuOption.getOrderItems().add(this);
        }
    }
}
