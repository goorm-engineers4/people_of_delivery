package com.example.cloudfour.peopleofdelivery.domain.menu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_menucategory")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)  // Builder만 사용하도록 제한
public class MenuCategory {
    @Id
    @GeneratedValue
    private UUID id;  // 자동 생성되는 UUID

    @Column(name = "category", nullable = false)
    private String category;

    @OneToMany(mappedBy = "menuCategory", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Menu> menus = new ArrayList<>();

    public static class MenuCategoryBuilder{
        private MenuCategoryBuilder id(UUID id){
            throw new UnsupportedOperationException("id 수정 불가");
        }
    }
}