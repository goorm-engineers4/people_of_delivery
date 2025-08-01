package com.example.cloudfour.peopleofdelivery.domain.store.entity;



import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @OneToMany(mappedBy = "storeCategory", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Store> stores = new ArrayList<>();

    public static class StoreCategoryBuilder {
        private StoreCategoryBuilder id(UUID id){
            throw new UnsupportedOperationException("id 생성 불가");
        }
    }
}
