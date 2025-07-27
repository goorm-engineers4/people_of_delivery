package com.example.cloudfour.peopleofdelivery.domain.store.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "p_storecategory")
public class StoreCategory {
    @Id
    @GeneratedValue
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String category;

    // 🔽 양방향 관계 추가
    @OneToMany(mappedBy = "storeCategory", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Store> stores = new ArrayList<>();

    public static class StoreCategoryBuilder {
        private StoreCategoryBuilder id(UUID id){
            throw new UnsupportedOperationException("id 생성 불가");
        }
    }
}
