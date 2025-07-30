package com.example.cloudfour.peopleofdelivery.domain.payment.service.command;

import com.example.cloudfour.peopleofdelivery.domain.payment.dto.PaymentRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.payment.dto.PaymentResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentCommandServiceImpl implements PaymentCommandService {

    @Override
    public PaymentResponseDTO.PaymentCreateResponseDTO createPayment(PaymentRequestDTO.PaymentCreateRequestDTO paymentCreateRequestDTO, User user) {
        return null;
    }

    @Override
    public PaymentResponseDTO.PaymentVerifyResponseDTO verifyPayment(PaymentRequestDTO.PaymentVerifyRequestDTO paymentVerifyRequestDTO, User user) {
        return null;
    }

    @Override
    public PaymentResponseDTO.PaymentUpdateResponseDTO updatePayment(PaymentRequestDTO.PaymentUpdateRequestDTO paymentUpdateRequestDTO, UUID orderId, User user) {
        return null;
    }

    @Override
    public PaymentResponseDTO.PaymentDeleteResponseDTO deletePayment(PaymentRequestDTO.PaymentDeleteRequestDTO paymentDeleteRequestDTO, UUID orderId, User user) {
        return null;
    }
}
