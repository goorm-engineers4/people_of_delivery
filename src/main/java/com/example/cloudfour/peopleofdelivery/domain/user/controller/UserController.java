package com.example.cloudfour.peopleofdelivery.domain.user.controller;

import com.example.cloudfour.peopleofdelivery.domain.user.service.UserAddressService;
import com.example.cloudfour.peopleofdelivery.domain.user.service.UserService;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.CustomResponse;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping('/api/users')
@Tag(name = "User", description = "유저 API by 모시은")
public class UserController {
    private final UserService userService;
    private final UserAddressService addressService;

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getMyInfo(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(userService.getMyInfo(user.getId()));
    }

    @PatchMapping("/me")
    public ResponseEntity<?> updateMyInfo(@AuthenticationPrincipal CustomUserDetails user,
                                          @RequestBody UserUpdateRequest request) {
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
            @RequestBody AddressRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        addressService.addAddress(userDetails.getUser().getId(), request);
        return CustomResponse.onSuccess(HttpStatus.CREATED, null);
    }

    @GetMapping("/addresses")
    public CustomResponse<List<AddressResponseDto>> getAddressList(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<AddressResponseDto> addressList = addressService.getAddresses(userDetails.getUser().getId());
        return CustomResponse.onSuccess(addressList);
    }

    @PatchMapping("/addresses/{addressId}")
    public CustomResponse<Void> updateAddress(
            @PathVariable Long addressId,
            @RequestBody AddressRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        addressService.updateAddress(userDetails.getUser().getId(), addressId, request);
        return CustomResponse.onSuccess(null);
    }

    @PatchMapping("/addresses/delete/{addressId}")
    public CustomResponse<Void> deleteAddress(
            @PathVariable Long addressId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        addressService.softDeleteAddress(userDetails.getUser().getId(), addressId);
        return CustomResponse.onSuccess(null);
    }

    @GetMapping("/me/cart")
    public CustomResponse<List<CartDto>> getMyCarts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (!userDetails.isCustomer()) {
            throw new UnauthorizedAccessException("고객만 장바구니를 조회할 수 있습니다.");
        }

        List<CartDto> carts = cartService.getCartsForUser(userDetails.getUser().getId());
        return CustomResponse.onSuccess(carts);
    }

    @GetMapping("/me/stores")
    public CustomResponse<List<StoreDto>> getMyStores(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (!userDetails.isOwner()) {
            throw new UnauthorizedAccessException("점주만 가게를 조회할 수 있습니다.");
        }

        List<StoreDto> stores = storeService.getStoresForOwner(userDetails.getUser().getId());
        return CustomResponse.onSuccess(stores);
    }
}
