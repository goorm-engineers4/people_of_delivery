package com.example.cloudfour.peopleofdelivery.domain.stores.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Stores {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private UUID storeId;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID regionID;

    @Column(nullable = false)
    private UUID storeCategoriesId;

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
    public Stores(UUID userId, UUID regionID, UUID storeCategoriesId, String name, String address,
                  String storePicture, String phone, String content, Integer minPrice, Integer deliveryTip,
                  Float rating, Integer likeCount, Integer reviewCount, String operationHours,
                  String closedDays) {
        this.userId = userId;
        this.regionID = regionID;
        this.storeCategoriesId = storeCategoriesId;
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
