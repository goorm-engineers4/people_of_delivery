package com.example.cloudfour.peopleofdelivery.domain.payment.service.command;

import com.example.cloudfour.peopleofdelivery.domain.payment.dto.PaymentRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.payment.dto.PaymentResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.payment.dto.TossWebhookPayload;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;

import java.util.UUID;

public interface PaymentCommandService {
    PaymentResponseDTO.PaymentCreateResponseDTO createPayment(PaymentRequestDTO.PaymentCreateRequestDTO request, User user);
    PaymentResponseDTO.PaymentVerifyResponseDTO verifyPayment(PaymentRequestDTO.PaymentVerifyRequestDTO request, User user);
    PaymentResponseDTO.PaymentUpdateResponseDTO updatePayment(PaymentRequestDTO.PaymentUpdateRequestDTO request, UUID orderId, User user);
    PaymentResponseDTO.PaymentDeleteResponseDTO deletePayment(PaymentRequestDTO.PaymentDeleteRequestDTO request, UUID orderId, User user);
    void updateStatusFromWebhook(TossWebhookPayload payload);
}
