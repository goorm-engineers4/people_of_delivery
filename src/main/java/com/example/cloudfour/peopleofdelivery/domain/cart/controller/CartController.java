package com.example.cloudfour.peopleofdelivery.domain.cart.controller;

import com.example.cloudfour.peopleofdelivery.domain.cart.dto.CartRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.cart.dto.CartResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.cart.service.command.CartCommandService;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartCommandService cartCommandService;

    @PostMapping
    @Operation(summary = "장바구니 생성", description = "장바구니를 생성합니다. 장바구니 생성에 사용되는 API입니다.")
    public CustomResponse<CartResponseDTO.CartCreateResponseDTO> createCart(
            @RequestBody CartRequestDTO.CartCreateRequestDTO cartCreateRequestDTO,
            @AuthenticationPrincipal User user
    ){
        CartResponseDTO.CartCreateResponseDTO cart = cartCommandService.createCart(cartCreateRequestDTO,user);
        return CustomResponse.onSuccess(HttpStatus.CREATED, cart);
    }

    @PatchMapping("/{cartId}/delete")
    @Operation(summary = "장바구니 삭제", description = "장바구니를 삭제합니다. 장바구니 삭제에 사용되는 API입니다.")
    public CustomResponse<String> deleteCart(
            @PathVariable("cartId") UUID cartId,
            @AuthenticationPrincipal User user
    ) {
        cartCommandService.deleteCart(cartId, user);
        return CustomResponse.onSuccess(HttpStatus.OK, "장바구니 삭제 완료");
    }


}
