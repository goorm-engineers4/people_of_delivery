package com.example.cloudfour.peopleofdelivery.domain.user.controller;

import com.example.cloudfour.peopleofdelivery.domain.auth.userdetails.CustomUserDetails;
import com.example.cloudfour.peopleofdelivery.domain.user.dto.UserRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.user.dto.UserResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.user.service.UserAddressService;
import com.example.cloudfour.peopleofdelivery.domain.user.service.UserService;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.CustomResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    public ResponseEntity<String> getMyInfo(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(userService.getMyInfo(user.getId()));
    }

    @PatchMapping("/me")
    public ResponseEntity<?> updateMyInfo(@AuthenticationPrincipal CustomUserDetails user,
                                          @RequestBody UserRequestDTO.UserUpdateRequestDTO request) {
        userService.updateMyInfo(user.getId(), request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/deleted")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal CustomUserDetails user) {
        userService.deleteAccount(user.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addresses")
    public CustomResponse<Void> addAddress(
            @RequestBody UserRequestDTO.AddressRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        addressService.addAddress(userDetails.getUser().getId(), request);
        return CustomResponse.onSuccess(HttpStatus.CREATED, null);
    }

    @GetMapping("/addresses")
    public CustomResponse<List<UserResponseDTO.AddressResponseDTO>> getAddressList(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<UserResponseDTO.AddressResponseDTO> addressList = addressService.getAddresses(userDetails.getUser().getId());
        return CustomResponse.onSuccess(addressList);
    }

    @PatchMapping("/addresses/{addressId}")
    public CustomResponse<Void> updateAddress(
            @PathVariable UUID addressId,
            @RequestBody UserRequestDTO.AddressRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        addressService.updateAddress(userDetails.getUser().getId(), addressId, request);
        return CustomResponse.onSuccess(null);
    }

    @PatchMapping("/addresses/delete/{addressId}")
    public CustomResponse<Void> deleteAddress(
            @PathVariable UUID addressId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        addressService.softDeleteAddress(userDetails.getUser().getId(), addressId);
        return CustomResponse.onSuccess(null);
    }
//
//    @GetMapping("/me/cart")
//    public CustomResponse<List<CartDto>> getMyCarts(@AuthenticationPrincipal CustomUserDetails userDetails) {
//        if (!userDetails.isCustomer()) {
//            throw new UnauthorizedAccessException("고객만 장바구니를 조회할 수 있습니다.");
//        }
//
//        List<CartDto> carts = cartService.getCartsForUser(userDetails.getUser().getId());
//        return CustomResponse.onSuccess(carts);
//    }
//
//    @GetMapping("/me/stores")
//    public CustomResponse<List<StoreDto>> getMyStores(@AuthenticationPrincipal CustomUserDetails userDetails) {
//        if (!userDetails.isOwner()) {
//            throw new UnauthorizedAccessException("점주만 가게를 조회할 수 있습니다.");
//        }
//
//        List<StoreDto> stores = storeService.getStoresForOwner(userDetails.getUser().getId());
//        return CustomResponse.onSuccess(stores);
//    }
}
