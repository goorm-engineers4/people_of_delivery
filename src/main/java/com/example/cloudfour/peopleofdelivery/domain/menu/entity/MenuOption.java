package com.example.cloudfour.peopleofdelivery.domain.menu.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "p_menuoptions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)  // Builder만 사용하도록 제한
public class MenuOption {
    @Id
    @UuidGenerator
    @Column(name = "menuOptionId")
    private String menuOptionId;  // 자동 생성되는 UUID

    @Column(name = "menuId", nullable = false)
    private String menuId;  // UUID를 String으로 처리

    @Column(name = "optionName", nullable = false)
    private String optionName;

    @Column(name = "additionalPrice", nullable = false)
    private Integer additionalPrice;  // INT 타입으로 변경

    // ID 제외한 필드들만 받는 정적 팩토리 메소드
    public static MenuOption createMenuOption(String menuId, String optionName, Integer additionalPrice) {
        if (menuId == null || menuId.trim().isEmpty()) {
            throw new IllegalArgumentException("메뉴ID는 필수입니다.");
        }
        if (optionName == null || optionName.trim().isEmpty()) {
            throw new IllegalArgumentException("옵션명은 필수입니다.");
        }
        if (additionalPrice == null || additionalPrice < 0) {
            throw new IllegalArgumentException("추가 가격은 0 이상이어야 합니다.");
        }

        return MenuOption.builder()
            .menuId(menuId)
            .optionName(optionName)
            .additionalPrice(additionalPrice)
            // menuOptionId는 자동 생성됨
            .build();
    }

    // 비즈니스 로직을 통한 안전한 변경 메소드
    public void updateOptionInfo(String optionName, Integer additionalPrice) {
        if (optionName == null || optionName.trim().isEmpty()) {
            throw new IllegalArgumentException("옵션명은 필수입니다.");
        }
        if (additionalPrice == null || additionalPrice < 0) {
            throw new IllegalArgumentException("추가 가격은 0 이상이어야 합니다.");
        }
        this.optionName = optionName;
        this.additionalPrice = additionalPrice;
    }
}