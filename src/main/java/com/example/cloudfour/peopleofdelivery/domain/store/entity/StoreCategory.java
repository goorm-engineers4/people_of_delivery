package com.example.cloudfour.peopleofdelivery.domain.store.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class StoreCategory {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private UUID storeCategoriesId;

    @Column(nullable = false, length = 255)
    private String category;

    @Builder
    public StoreCategory(String category) {
        this.category = category;
    }
}

