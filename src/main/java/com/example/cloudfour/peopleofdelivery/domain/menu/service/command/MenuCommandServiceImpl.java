package com.example.cloudfour.peopleofdelivery.domain.menu.service.command;

import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MenuCommandServiceImpl {

    public MenuResponseDTO.MenuDetailResponseDTO createMenu(MenuRequestDTO.MenuCreateRequestDTO requestDTO, User user) {
        return null;
    }

    public MenuResponseDTO.MenuDetailResponseDTO updateMenu(UUID menuId, MenuRequestDTO.MenuUpdateRequestDTO requestDTO, User user) {
        return null;
    }

    public void deleteMenu(UUID menuId, User user) {
    }
}
