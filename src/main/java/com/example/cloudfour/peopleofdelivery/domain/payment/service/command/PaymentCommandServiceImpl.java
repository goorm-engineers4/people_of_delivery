package com.example.cloudfour.peopleofdelivery.domain.payment.service.command;

import com.example.cloudfour.peopleofdelivery.domain.order.entity.Order;
import com.example.cloudfour.peopleofdelivery.domain.order.repository.OrderRepository;
import com.example.cloudfour.peopleofdelivery.domain.payment.apiclient.TossApiClient;
import com.example.cloudfour.peopleofdelivery.domain.payment.dto.PaymentRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.payment.dto.PaymentResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.payment.dto.TossApproveResponse;
import com.example.cloudfour.peopleofdelivery.domain.payment.dto.TossWebhookPayload;
import com.example.cloudfour.peopleofdelivery.domain.payment.entity.Payment;
import com.example.cloudfour.peopleofdelivery.domain.payment.enums.PaymentStatus;
import com.example.cloudfour.peopleofdelivery.domain.payment.exception.PaymentErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.payment.exception.PaymentException;
import com.example.cloudfour.peopleofdelivery.domain.payment.repository.PaymentHistoryRepository;
import com.example.cloudfour.peopleofdelivery.domain.payment.repository.PaymentRepository;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCommandServiceImpl implements PaymentCommandService {

    private final PaymentRepository paymentRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final OrderRepository orderRepository;
    private final TossApiClient tossApiClient;

    @Value("${toss.success-url}")
    private String successUrl;

    @Value("${toss.fail-url}")
    private String failUrl;

    @Override
    @Transactional
    public PaymentResponseDTO.PaymentCreateResponseDTO createPayment(PaymentRequestDTO.PaymentCreateRequestDTO request, User user) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND));


        int totalPrice = order.getTotalPrice();

        String paymentUrl = String.format(
                "https://pay.toss.im/pay?amount=%d&orderId=%s&orderName=%s&customerName=%s&successUrl=%s&failUrl=%s",
                totalPrice,
                order.getId().toString(),
                "주문결제",
                user.getNickname(),
                successUrl,
                failUrl
        );

        return PaymentResponseDTO.PaymentCreateResponseDTO.builder()
                .paymentUrl(paymentUrl)
                .build();
    }

    @Override
    @Transactional
    public PaymentResponseDTO.PaymentVerifyResponseDTO verifyPayment(PaymentRequestDTO.PaymentVerifyRequestDTO request, User user) {


        if (paymentRepository.existsByPaymentKey(request.getPaymentKey())) {
            throw new PaymentException(PaymentErrorCode.PAYMENT_ALREADY_APPROVED, "이미 승인된 결제입니다.");
        }

        TossApproveResponse tossResponse = tossApiClient.approvePayment(
                request.getPaymentKey(),
                request.getOrderId(),
                request.getAmount()
        );

        Order order = orderRepository.findById(UUID.fromString(request.getOrderId()))
                .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new PaymentException(PaymentErrorCode.UNAUTHORIZED_PAYMENT_ACCESS);
        }

        Payment payment = Payment.builder()
                .paymentKey(tossResponse.getPaymentKey())
                .tossOrderId(tossResponse.getOrderId())
                .totalPrice(tossResponse.getTotalAmount())
                .paymentMethod(tossResponse.getMethod())
                .paymentStatus(PaymentStatus.APPROVED)
                .order(order)
                .build();

        var history = payment.addHistory(null, PaymentStatus.APPROVED, "Toss 결제 승인 완료");
        paymentRepository.save(payment);
        paymentHistoryRepository.save(history);

        return PaymentResponseDTO.PaymentVerifyResponseDTO.builder()
                .paymentKey(payment.getPaymentKey())
                .orderId(payment.getTossOrderId())
                .amount(payment.getTotalPrice())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .build();
    }

    @Override
    @Transactional
    public PaymentResponseDTO.PaymentUpdateResponseDTO updatePayment(PaymentRequestDTO.PaymentUpdateRequestDTO request, UUID orderId, User user) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        PaymentStatus previous = payment.getPaymentStatus();
        payment.setPaymentStatus(request.getPaymentStatus());
        var history = payment.addHistory(previous, request.getPaymentStatus(), "관리자 상태 변경");
        paymentHistoryRepository.save(history);

        return PaymentResponseDTO.PaymentUpdateResponseDTO.builder()
                .paymentId(payment.getId())
                .updatedStatus(payment.getPaymentStatus())
                .build();
    }

    @Override
    @Transactional
    public PaymentResponseDTO.PaymentCancelResponseDTO cancelPayment(PaymentRequestDTO.PaymentCancelRequestDTO request, UUID orderId, User user) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        if (!payment.getOrder().getUser().getId().equals(user.getId())) {
            throw new PaymentException(PaymentErrorCode.UNAUTHORIZED_PAYMENT_ACCESS);
        }

        if (payment.getPaymentStatus() != PaymentStatus.APPROVED) {
            throw new PaymentException(PaymentErrorCode.INVALID_PAYMENT_STATUS, "승인된 결제만 취소할 수 있습니다.");
        }

        tossApiClient.cancelPayment(payment.getPaymentKey(), request.getCancelReason());

        PaymentStatus previous = payment.getPaymentStatus();
        payment.setPaymentStatus(PaymentStatus.CANCELED);
        payment.setFailedReason(request.getCancelReason());
        var history = payment.addHistory(previous, PaymentStatus.CANCELED, request.getCancelReason());
        paymentHistoryRepository.save(history);

        return PaymentResponseDTO.PaymentCancelResponseDTO.builder()
                .paymentId(payment.getId())
                .status(payment.getPaymentStatus())
                .cancelReason(request.getCancelReason())
                .build();
    }

    @Override
    @Transactional
    public void updateStatusFromWebhook(TossWebhookPayload payload) {
        Payment payment = paymentRepository.findByPaymentKey(payload.getPaymentKey())
                .orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        PaymentStatus newStatus = convertTossStatus(payload.getStatus());

        if (payment.getPaymentStatus() == newStatus) {
            log.info("[Webhook] 중복 상태 요청 무시: {}", newStatus);
            return;
        }

        PaymentStatus previous = payment.getPaymentStatus();
        payment.setPaymentStatus(newStatus);

        if (newStatus == PaymentStatus.CANCELED || newStatus == PaymentStatus.FAILED) {
            payment.setFailedReason("Webhook 상태: " + payload.getStatus());
        }

        var history = payment.addHistory(previous, newStatus, "토스 웹훅 상태 변경");
        paymentHistoryRepository.save(history);

        log.info("[Webhook] 결제 상태 업데이트 완료: {} → {}", previous, newStatus);
    }

    private PaymentStatus convertTossStatus(String tossStatus) {
        return switch (tossStatus) {
            case "DONE", "APPROVED" -> PaymentStatus.APPROVED;
            case "CANCELED" -> PaymentStatus.CANCELED;
            case "FAILED" -> PaymentStatus.FAILED;
            default -> throw new PaymentException(PaymentErrorCode.TOSS_STATUS_UNKNOWN, "Unknown status: " + tossStatus);
        };
    }
}
