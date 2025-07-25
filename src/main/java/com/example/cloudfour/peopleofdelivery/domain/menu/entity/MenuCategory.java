package com.example.cloudfour.peopleofdelivery.domain.menu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "p_menucategories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuCategory {
    @Id
    @Column(name = "menuCategories")
    private String menuCategories;

    @Column(name = "category", nullable = false)
    private String category;
}