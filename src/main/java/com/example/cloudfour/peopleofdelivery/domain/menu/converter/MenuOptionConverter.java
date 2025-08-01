package com.example.cloudfour.peopleofdelivery.domain.menu.converter;

import com.example.cloudfour.peopleofdelivery.domain.menu.dto.MenuOptionResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.menu.entity.MenuOption;

public class MenuOptionConverter {
    public static MenuOptionResponseDTO.MenuOptionListResponseDTO toMenuOptionListResponseDTO(MenuOption option) {
        return MenuOptionResponseDTO.MenuOptionListResponseDTO.builder()
                .menuId(option.getMenu().getId())
                .optionName(option.getOptionName())
                .additionalPrice(option.getAdditionalPrice())
                .build();
    }
}
