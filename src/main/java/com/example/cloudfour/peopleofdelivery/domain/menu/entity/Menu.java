package com.example.cloudfour.peopleofdelivery.domain.menu.entity;

import com.example.cloudfour.peopleofdelivery.domain.cartitem.entity.CartItem;
import com.example.cloudfour.peopleofdelivery.domain.menu.enums.MenuStatus;
import com.example.cloudfour.peopleofdelivery.domain.menu.exception.MenuErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.menu.exception.MenuException;
import com.example.cloudfour.peopleofdelivery.domain.order.entity.OrderItem;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_menu")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Menu {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "menuPicture", columnDefinition = "TEXT")
    private String menuPicture;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MenuStatus status;

    @CreationTimestamp
    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menuCategoryId", nullable = false)
    private MenuCategory menuCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeId", nullable = false)
    private Store store;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "menu")
    @Builder.Default
    private List<MenuOption> menuOptions = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "menu")
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "menu")
    @Builder.Default
    private List<CartItem> cartItems = new ArrayList<>();

    // Builder에서 id 필드 제외
    public static class MenuBuilder {
        private MenuBuilder id(UUID id) {
            throw new MenuException(MenuErrorCode.CREATE_FAILED);
        }
    }

    public void setMenuCategory(MenuCategory menuCategory){
        this.menuCategory = menuCategory;
        menuCategory.getMenus().add(this);
    }

    public void setStore(Store store){
        this.store = store;
        store.getMenus().add(this);
    }

    // 메뉴 정보 수정을 위한 업데이트 메서드
    public void updateMenuInfo(String name, String content, Integer price, String menuPicture) {
        this.name = name;
        this.content = content;
        this.price = price;
        this.menuPicture = menuPicture;
    }

    public void updateStatus(MenuStatus status) {
        this.status = status;
    }
}