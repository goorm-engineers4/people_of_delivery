package com.example.cloudfour.peopleofdelivery.domain.cartitem.entity;

import com.example.cloudfour.peopleofdelivery.domain.cart.entity.Cart;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.dto.CartItemRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.Menu;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuOption;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "p_cartitem")
public class CartItem {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cartId" ,nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menuId" ,nullable = false)
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menuOptionId" ,nullable = false)
    private MenuOption menuOption;

    public static class CartItemBuilder{
        private CartItemBuilder id(UUID id){
            throw new UnsupportedOperationException("id 수정 불가");
        }
    }

    public void setCart(Cart cart){
        this.cart = cart;
        cart.getCartItems().add(this);
    }

    public void setMenu(Menu menu){
        this.menu = menu;
        menu.getCartItems().add(this);
    }

    public void setMenuOption(MenuOption menuOption){
        this.menuOption = menuOption;
        menuOption.getCartItems().add(this);
    }

    public void update(Integer quantity, Integer price){
        if (quantity != null) this.quantity = quantity;
        if(price != null) this.price = price;
    }
}
