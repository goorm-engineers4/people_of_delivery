package com.example.cloudfour.peopleofdelivery.domain.payment.exception;

import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.code.BaseErrorCode;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.exception.CustomException;

public class PaymentHistoryException extends CustomException {
    public PaymentHistoryException(BaseErrorCode errorCode) {
      super(errorCode);
    }
}
