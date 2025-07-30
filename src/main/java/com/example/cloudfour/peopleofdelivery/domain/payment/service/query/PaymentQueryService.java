package com.example.cloudfour.peopleofdelivery.domain.payment.service.query;

import com.example.cloudfour.peopleofdelivery.domain.payment.dto.PaymentResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.user.entity.User;

import java.util.UUID;

public interface PaymentQueryService {
    PaymentResponseDTO.PaymentDetailResponseDTO  getDetailPayment(UUID orderId, User user);
    PaymentResponseDTO.PaymentStoreListResponseDTO  getStoreListPayment(UUID storeId, User user);
    PaymentResponseDTO.PaymentUserListResponseDTO  getUserListPayment(User user);
    PaymentResponseDTO.PaymentStoreSummaryResponseDTO getStoreSummaryPayment(UUID storeId, User user);
}
