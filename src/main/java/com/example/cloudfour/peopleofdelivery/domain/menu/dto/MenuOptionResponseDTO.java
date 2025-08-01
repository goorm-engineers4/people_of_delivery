package com.example.cloudfour.peopleofdelivery.domain.menu.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

public class MenuOptionResponseDTO {
    @Getter
    @Builder
    public static class MenuOptionListResponseDTO{
        UUID menuId;
        String optionName;
        Integer additionalPrice;
    }
}
