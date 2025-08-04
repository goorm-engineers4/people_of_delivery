package com.example.cloudfour.peopleofdelivery.domain.store.converter;

import com.example.cloudfour.peopleofdelivery.domain.store.entity.StoreCategory;

public class StoreCategoryConverter {
    public static StoreCategory toStoreCategory(String categoryName) {
        return StoreCategory.builder()
                .category(categoryName)
                .build();
    }
}
