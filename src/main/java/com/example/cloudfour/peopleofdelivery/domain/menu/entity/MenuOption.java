package com.example.cloudfour.peopleofdelivery.domain.menu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "p_menuoptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuOption {
    @Id
    @Column(name = "menuOptionId")
    private String menuOptionId;

    @Column(name = "menuId", nullable = false)
    private String menuId;

    @Column(name = "content", nullable = false)
    private String content;
}