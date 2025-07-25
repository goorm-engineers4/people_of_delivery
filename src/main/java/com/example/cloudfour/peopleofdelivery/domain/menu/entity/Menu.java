package com.example.cloudfour.peopleofdelivery.domain.menu.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "p_menus")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)  // Builder만 사용하도록 제한
public class Menu {
    @Id
    @UuidGenerator
    @Column(name = "menuId")
    private String menuId;  // 자동 생성되는 UUID

    @Column(name = "storeId", nullable = false)
    private String storeId;  // UUID를 String으로 처리

    @Column(name = "menuCategories", nullable = false)
    private String menuCategories;  // UUID를 String으로 처리

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "price", nullable = false)
    private Integer price;  // INT 타입으로 변경

    @Column(name = "menuPicture", columnDefinition = "TEXT")
    private String menuPicture;  // TEXT 타입으로 변경

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MenuStatus status;

    @CreationTimestamp
    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;  // TIMESTAMP 타입

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;  // TIMESTAMP 타입

    // ID 제외한 필드들만 받는 정적 팩토리 메소드
    public static Menu createMenu(String storeId, String menuCategories, String name,
                                 String content, Integer price, MenuStatus status) {
        if (storeId == null || storeId.trim().isEmpty()) {
            throw new IllegalArgumentException("가게ID는 필수입니다.");
        }
        if (menuCategories == null || menuCategories.trim().isEmpty()) {
            throw new IllegalArgumentException("메뉴카테고리ID는 필수입니다.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("메뉴명은 필수입니다.");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("메뉴 설명은 필수입니다.");
        }
        if (price == null || price <= 0) {
            throw new IllegalArgumentException("가격은 0보다 커야 합니다.");
        }
        if (status == null) {
            throw new IllegalArgumentException("메뉴 상태는 필수입니다.");
        }

        return Menu.builder()
            .storeId(storeId)
            .menuCategories(menuCategories)
            .name(name)
            .content(content)
            .price(price)
            .status(status)
            // menuId, createdAt, updatedAt은 자동 생성됨
            .build();
    }

    // 비즈니스 로직을 통한 안전한 변경 메소드들
    public void updateMenuInfo(String name, String content, Integer price) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("메뉴명은 필수입니다.");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("메뉴 설명은 필수입니다.");
        }
        if (price == null || price <= 0) {
            throw new IllegalArgumentException("가격은 0보다 커야 합니다.");
        }

        this.name = name;
        this.content = content;
        this.price = price;
    }

    public void updateMenuPicture(String menuPicture) {
        this.menuPicture = menuPicture;
    }

    public void updateStatus(MenuStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("메뉴 상태는 필수입니다.");
        }
        this.status = status;
    }
}