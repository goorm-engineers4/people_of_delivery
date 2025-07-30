package com.example.cloudfour.peopleofdelivery.domain.menu.dto;

import com.example.cloudfour.peopleofdelivery.domain.menu.enums.MenuStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// MenuRequestDTO: 클라이언트 → 서버로 보내는 데이터
public class MenuRequestDTO {

    // 메뉴 생성할 때 필요한 데이터
    @Getter // Lombok 어노테이션으로 getter 메서드 자동 생성
    @Builder // 빌더 패턴을 사용하여 객체 생성
    @NoArgsConstructor // 기본 생성자 자동 생성
    @AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자 자동 생성
    public static class MenuCreateRequestDTO {
        private String name;
        private String content;
        private Integer price;
        private String menuPicture;
        private MenuStatus status;
        private String category;
    }

    // 메뉴 수정할 때 필요한 데이터
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuUpdateRequestDTO {
        private String name;
        private String content;
        private Integer price;
        private String menuPicture;
        private MenuStatus status;
        private String category;
    }
}
