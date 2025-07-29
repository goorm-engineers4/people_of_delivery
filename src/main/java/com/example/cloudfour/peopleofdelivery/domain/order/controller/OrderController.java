package com.example.cloudfour.peopleofdelivery.domain.order.controller;

import com.example.cloudfour.peopleofdelivery.domain.order.dto.OrderRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.order.dto.OrderResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.order.service.command.OrderCommandService;
import com.example.cloudfour.peopleofdelivery.domain.order.service.query.OrderQueryService;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@Tag(name = "Order", description = "주문 API by 김준형")
public class OrderController {
    private final OrderCommandService orderCommandService;
    private final OrderQueryService orderQueryService;

    @PostMapping("/")
    @Operation(summary = "주문 생성", description = "주문을 생성합니다. 주문 생성에 사용되는 API입니다.")
    public CustomResponse<OrderResponseDTO.OrderCreateResponseDTO> createOrder(
            @RequestBody OrderRequestDTO.OrderCreateRequestDTO orderCreateRequestDTO,
            @AuthenticationPrincipal User user
    ){
        OrderResponseDTO.OrderCreateResponseDTO order = orderCommandService.createOrder(orderCreateRequestDTO,user);
        return CustomResponse.onSuccess(HttpStatus.CREATED, order);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "주문 상세 조회", description = "주문을 상세 조회합니다. 주문 상세 조회에 사용되는 API입니다.")
    public CustomResponse<OrderResponseDTO.OrderDetailResponseDTO> getOrder(
            @PathVariable("orderId") UUID orderId,
            @AuthenticationPrincipal User user
    ){
        OrderResponseDTO.OrderDetailResponseDTO order = orderQueryService.getOrderById(orderId,user);
        return CustomResponse.onSuccess(HttpStatus.OK, order);
    }

    @GetMapping("/me")
    @Operation(summary = "내 주문 내역 조회", description = "내 주문 내역을 조회합니다. 내 주문 내역 조회에 사용되는 API입니다.")
    public CustomResponse<List<OrderResponseDTO.OrderUserListResponseDTO>> getMyOrder(
            @AuthenticationPrincipal User user
    ){
        List<OrderResponseDTO.OrderUserListResponseDTO> order = orderQueryService.getOrderListByUser(user);
        return CustomResponse.onSuccess(HttpStatus.OK, order);
    }

    @GetMapping("/{storeId}/orders")
    @Operation(summary = "가게 주문 조회", description = "가게 주문을 조회합니다. 가게 주문 조회에 사용되는 API입니다.")
    public CustomResponse<List<OrderResponseDTO.OrderStoreListResponseDTO>> getStoreOrder(
            @PathVariable("storeId") UUID storeId,
            @AuthenticationPrincipal User user
    ){
        List<OrderResponseDTO.OrderStoreListResponseDTO> order = orderQueryService.getOrderListByStore(storeId,user);
        return CustomResponse.onSuccess(HttpStatus.OK, order);
    }

    @PatchMapping("/{orderId}/status")
    @Operation(summary = "주문 상태 변경", description = "주문 상태를 변경합니다. 주문 상태 변경에 사용되는 API입니다.")
    public CustomResponse<OrderResponseDTO.OrderUpdateResponseDTO>  updateOrderStatus(
            @RequestBody OrderRequestDTO.OrderUpdateRequestDTO orderUpdateRequestDTO,
            @PathVariable("orderId") UUID orderId,
            @AuthenticationPrincipal User user
    ){
        OrderResponseDTO.OrderUpdateResponseDTO order = orderCommandService.updateOrder(orderUpdateRequestDTO,orderId,user);
        return CustomResponse.onSuccess(HttpStatus.OK, order);
    }

    @PatchMapping("/{orderId}/canceled")
    @Operation(summary = "주문 취소", description = "주문을 취소합니다. 주문 취소에 사용되는 API입니다.")
    public CustomResponse<String>  deleteOrder(
            @PathVariable("orderId") UUID orderId,
            @AuthenticationPrincipal User user
    ){
        orderCommandService.deleteOrder(orderId,user);
        return CustomResponse.onSuccess(HttpStatus.OK, "주문 취소 완료.");
    }

    @GetMapping("/{orderId}/history")
    @Operation(summary = "주문 상태 이력 조회", description = "주문 상태 이력을 조회합니다. 주문 상태 이력 조회에 사용되는 API입니다.")
    public CustomResponse<OrderResponseDTO.OrderStatusResponseDTO> getOrderStatus(
            @PathVariable("orderId") UUID orderId,
            @AuthenticationPrincipal User user
    ){
        OrderResponseDTO.OrderStatusResponseDTO order = orderQueryService.getOrderStatusById(orderId,user);
        return CustomResponse.onSuccess(HttpStatus.OK, order);
    }

}
