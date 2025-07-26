package com.example.cloudfour.peopleofdelivery.domain.menu.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "p_menucategories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)  // Builder만 사용하도록 제한
public class MenuCategory {
    @Id
    @UuidGenerator
    @Column(name = "menuCategories")
    private String menuCategories;  // 자동 생성되는 UUID

    @Column(name = "category", nullable = false)
    private String category;

    @OneToMany(mappedBy = "menuCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Menu> menus = new java.util.ArrayList<>();

    // ID 제외한 필드들만 받는 정적 팩토리 메소드
    public static MenuCategory createMenuCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("카테고리명은 필수입니다.");
        }

        return MenuCategory.builder()
            .category(category)
            // menuCategories는 자동 생성됨
            .build();
    }

    // 비즈니스 로직을 통한 안전한 변경 메소드
    public void updateCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("카테고리명은 필수입니다.");
        }
        this.category = category;
    }
}