package com.example.cloudfour.peopleofdelivery.domain.user.controller;

import com.example.cloudfour.peopleofdelivery.global.auth.userdetails.CustomUserDetails;
import com.example.cloudfour.peopleofdelivery.domain.user.dto.UserRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.user.dto.UserResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.user.service.UserAddressService;
import com.example.cloudfour.peopleofdelivery.domain.user.service.UserService;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User", description = "유저 API by 모시은")
public class UserController {
    private final UserService userService;
    private final UserAddressService addressService;

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "내 계정의 상세 정보를 조회합니다.")
    public CustomResponse<UserResponseDTO.MeResponseDTO> getMyInfo(@AuthenticationPrincipal CustomUserDetails user) {
        return CustomResponse.onSuccess(userService.getMyInfo(user.getId()));
    }

    @PatchMapping("/me")
    @Operation(summary = "내 정보 수정", description = "내 계정의 상세 정보를 수정합니다.")
    public CustomResponse<Void> updateMyInfo(@AuthenticationPrincipal CustomUserDetails user,
                                             @Valid @RequestBody UserRequestDTO.UserUpdateRequestDTO request) {
        userService.updateProfile(user.getId(), request.nickname(), request.number());
        return CustomResponse.onSuccess(null);
    }

    @PatchMapping("/deleted")
    @Operation(summary = "내 계정 삭제", description = "내 계정을 삭제합니다.")
    public CustomResponse<Void> deleteAccount(@AuthenticationPrincipal CustomUserDetails user) {
        userService.deleteAccount(user.getId());
        return CustomResponse.onSuccess(null);
    }

    @PostMapping("/addresses")
    @Operation(summary = "내 주소 등록", description = "내 주소를 등록합니다.")
    public CustomResponse<Void> addAddress(@Valid @RequestBody UserRequestDTO.AddressRequestDTO request,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        addressService.addAddress(userDetails.getId(), request);
        return CustomResponse.onSuccess(HttpStatus.CREATED, null); // 201
    }

    @GetMapping("/addresses")
    @Operation(summary = "내 주소 조회", description = "내 주소를 조회합니다.")
    public CustomResponse<List<UserResponseDTO.AddressResponseDTO>> getAddressList(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<UserResponseDTO.AddressResponseDTO> list = addressService.getAddresses(userDetails.getId());
        return CustomResponse.onSuccess(list);
    }

    @PatchMapping("/addresses/{addressId}")
    @Operation(summary = "내 주소 수정", description = "내 주소를 수정합니다.")
    public CustomResponse<Void> updateAddress(@PathVariable UUID addressId,
                                              @Valid @RequestBody UserRequestDTO.AddressRequestDTO request,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        addressService.updateAddress(userDetails.getId(), addressId, request);
        return CustomResponse.onSuccess(null);
    }

    @PatchMapping("/addresses/delete/{addressId}")
    @Operation(summary = "내 주소 삭제", description = "내 주소를 삭제합니다.")
    public CustomResponse<Void> deleteAddress(@PathVariable UUID addressId,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        addressService.deleteAddress(userDetails.getId(), addressId);
        return CustomResponse.onSuccess(null);
    }
}
