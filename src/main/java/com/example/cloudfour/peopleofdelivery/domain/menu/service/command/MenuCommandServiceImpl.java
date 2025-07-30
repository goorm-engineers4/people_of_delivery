package com.example.cloudfour.peopleofdelivery.domain.menu.service.command;

import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service // 서비스 레이어 어노테이션
@Slf4j // 로그 찍기 기능
@RequiredArgsConstructor // 생성자 주입을 위한 어노테이션
@Transactional // 트랜잭션 관리 어노테이션
public class MenuCommandServiceImpl {

    // 메뉴 생성
    public MenuResponseDTO.MenuDetailResponseDTO createMenu(MenuRequestDTO.MenuCreateRequestDTO requestDTO, User user) {
        // 권한 검사, 사업자의 가게 조회, 메뉴명 중복 체크, 이미지 업로드, 메뉴 카테고리 조회 또는 생성, 엔티티 생성 및 저장, 응답 DTO 생성
        return null;
    }

    // 메뉴 수정
    public MenuResponseDTO.MenuDetailResponseDTO updateMenu(UUID menuId, MenuRequestDTO.MenuUpdateRequestDTO requestDTO, User user) {
        // 메뉴 존재 확인, 권한 체크
        return null;
    }

    // 메뉴 삭제
    public void deleteMenu(UUID menuId, User user) {
        // 권한 체크, 소프트 삭제
    }
}
