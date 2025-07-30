package com.example.cloudfour.peopleofdelivery.domain.menu.service.query;

import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service // 서비스 레이어 어노테이션
@Slf4j // 로그 찍기 기능
@RequiredArgsConstructor // 생성자 주입을 위한 어노테이션
@Transactional(readOnly = true) // 트랜잭션 관리 어노테이션
public class MenuQueryServiceImpl {

    // 특정 가게의 메뉴 목록 조회
    public List<MenuResponseDTO.MenuListResponseDTO> getMenusByStore(UUID storeId) {
        // 가게 존재 확인, 활성 상태 메뉴만 조회, DTO 변환
        return null;
    }

    // 전체 인기 메뉴 TOP20 조회
    public List<MenuResponseDTO.MenuTopResponseDTO> getTopMenus() {
        return null;
    }

    // 시간대별 인기 메뉴 TOP20 조회
    public List<MenuResponseDTO.MenuTimeTopResponseDTO> getTimeTopMenus() {
        return null;
    }

    // 지역별 인기 메뉴 TOP20 조회
    public List<MenuResponseDTO.MenuRegionTopResponseDTO> getRegionTopMenus() {
        return null;
    }

    // 메뉴 상세 조회
    public MenuResponseDTO.MenuDetailResponseDTO getMenuDetail(UUID menuId) {
        return null;
    }
}
