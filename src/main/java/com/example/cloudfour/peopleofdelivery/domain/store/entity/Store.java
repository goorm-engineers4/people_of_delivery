package com.example.cloudfour.peopleofdelivery.domain.store.entity;

import com.example.cloudfour.peopleofdelivery.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@Table(name = "p_store")
public class Store extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeCategoriesId", nullable = false)
    private StoreCategory storeCategory;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 255)
    private String address;

    private String storePicture;

    @Column(nullable = false, length = 255)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Integer minPrice;

    @Column(nullable = false)
    private Integer deliveryTip;

    private Float rating;

    private Integer likeCount;

    private Integer reviewCount;

    @Column(nullable = false, length = 255)
    private String operationHours;

    @Column(nullable = false, length = 255)
    private String closedDays;
}
