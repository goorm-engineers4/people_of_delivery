package com.example.cloudfour.peopleofdelivery.domain.store.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "stores")
public class StoreCategory {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private UUID storeCategoriesId;

    @Column(nullable = false, length = 255)
    private String category;

    // 🔽 양방향 관계 추가
    @OneToMany(mappedBy = "storeCategory", cascade = CascadeType.ALL)
    private List<Store> stores = new ArrayList<>();

    @Builder
    public StoreCategory(String category) {
        this.category = category;
    }
}
