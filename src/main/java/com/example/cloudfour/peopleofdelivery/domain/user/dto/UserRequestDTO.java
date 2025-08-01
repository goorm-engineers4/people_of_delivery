package com.example.cloudfour.peopleofdelivery.domain.user.dto;

import com.example.cloudfour.peopleofdelivery.domain.region.entity.Region;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.LoginType;
import com.example.cloudfour.peopleofdelivery.domain.user.enums.Role;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

public class UserRequestDTO {
    public record UserUpdateRequestDTO(
            @Size(min=2, max=20)
            String nickname,

            @Pattern(regexp="^[0-9\\-]{7,20}$", message="전화번호 형식이 올바르지 않습니다.")
            String number
    ) {}

    public record AddressRequestDTO(
            @NotBlank
            @Size(min = 2, max = 500)
            String address,

            @NotNull
            UUID regionId
    ) {}

}
