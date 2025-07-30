package com.example.cloudfour.peopleofdelivery.domain.menu.service.query;

import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuQueryServiceImpl {

    public List<MenuResponseDTO.MenuListResponseDTO> getMenusByStore(UUID storeId) {
        return null;
    }

    public List<MenuResponseDTO.MenuTopResponseDTO> getTopMenus() {
        return null;
    }

    public List<MenuResponseDTO.MenuTimeTopResponseDTO> getTimeTopMenus() {
        return null;
    }

    public List<MenuResponseDTO.MenuRegionTopResponseDTO> getRegionTopMenus() {
        return null;
    }

    public MenuResponseDTO.MenuDetailResponseDTO getMenuDetail(UUID menuId) {
        return null;
    }
}
