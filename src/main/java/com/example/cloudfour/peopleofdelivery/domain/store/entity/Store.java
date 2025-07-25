package com.example.cloudfour.peopleofdelivery.domain.store.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "storeCategory")
public class Store {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private UUID storeId;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID regionID;

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

    @Builder
    public Store(UUID userId, UUID regionID, StoreCategory  storeCategory , String name, String address,
                 String storePicture, String phone, String content, Integer minPrice, Integer deliveryTip,
                 Float rating, Integer likeCount, Integer reviewCount, String operationHours,
                 String closedDays) {
        this.userId = userId;
        this.regionID = regionID;
        this.storeCategory  = storeCategory ;
        this.name = name;
        this.address = address;
        this.storePicture = storePicture;
        this.phone = phone;
        this.content = content;
        this.minPrice = minPrice;
        this.deliveryTip = deliveryTip;
        this.rating = rating;
        this.likeCount = likeCount;
        this.reviewCount = reviewCount;
        this.operationHours = operationHours;
        this.closedDays = closedDays;
    }
}
