package com.example.cloudfour.peopleofdelivery.domain.payment.service.query;

import com.example.cloudfour.peopleofdelivery.domain.payment.dto.PaymentResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentQueryServiceImpl implements PaymentQueryService {
    @Override
    public PaymentResponseDTO.PaymentDetailResponseDTO getDetailPayment(UUID orderId, User user) {
        return null;
    }

    @Override
    public PaymentResponseDTO.PaymentStoreListResponseDTO getStoreListPayment(UUID storeId, User user) {
        return null;
    }

    @Override
    public PaymentResponseDTO.PaymentUserListResponseDTO getUserListPayment(User user) {
        return null;
    }

    @Override
    public PaymentResponseDTO.PaymentStoreSummaryResponseDTO getStoreSummaryPayment(UUID storeId, User user) {
        return null;
    }
}
