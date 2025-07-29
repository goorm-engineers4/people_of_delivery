package com.example.cloudfour.peopleofdelivery.domain.cartitem.controller;

import com.example.cloudfour.peopleofdelivery.domain.cartitem.dto.CartItemRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.dto.CartItemResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.service.command.CartItemCommandService;
import com.example.cloudfour.peopleofdelivery.domain.cartitem.service.query.CartItemQueryService;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cartItem")
@RequiredArgsConstructor
public class CartItemController {
    private final CartItemCommandService cartItemCommandService;
    private final CartItemQueryService cartItemIdQueryService;

    @GetMapping("/{cartItemId}")
    @Operation(summary = "장바구니 항목 조회", description = "장바구니 항목을 조회합니다. 장바구니 항목 조회에 사용되는 API입니다.")
    public CustomResponse<CartItemResponseDTO.CartItemListResponseDTO> getCartItem(
            @PathVariable("cartItemId") UUID cartItemId,
            @AuthenticationPrincipal User user
    ){
        CartItemResponseDTO.CartItemListResponseDTO cartItem = cartItemIdQueryService.getCartItemById(cartItemId,user);
        return CustomResponse.onSuccess(HttpStatus.OK, cartItem);
    }

    @PatchMapping("/{cartItemId}")
    @Operation(summary = "장바구니 항목, 옵션 수정", description = "장바구니 항목, 옵션을 수정합니다. 장바구니 항목, 옵션 수정에 사용되는 API입니다.")
    public CustomResponse<CartItemResponseDTO.CartItemUpdateResponseDTO> updateCartItem(
            @PathVariable("cartItemId") UUID cartItemId,
            @RequestBody CartItemRequestDTO.CartItemUpdateRequestDTO request,
            @AuthenticationPrincipal User user
    ){
        CartItemResponseDTO.CartItemUpdateResponseDTO cartItem = cartItemCommandService.updateCartItem(request, cartItemId, user);
        return CustomResponse.onSuccess(HttpStatus.OK, cartItem);
    }

    @PatchMapping("/{cartItemId}/delete")
    @Operation(summary = "장바구니 항목 삭제", description = "장바구니 항목을 삭제합니다. 장바구니 항목 삭제에 사용되는 API입니다.")
    public CustomResponse<String> deleteCartItem(
            @PathVariable("cartItemId") UUID cartItemId,
            @AuthenticationPrincipal User user
    ) {
        cartItemCommandService.deleteCartItem(cartItemId, user);
        return CustomResponse.onSuccess(HttpStatus.OK, "장바구니 항목 삭제 완료");
    }
}
