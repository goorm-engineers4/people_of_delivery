package com.example.cloudfour.peopleofdelivery.domain.payment.exception;

import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.code.BaseErrorCode;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.exception.CustomException;

public class PaymentException extends CustomException {
    public PaymentException(BaseErrorCode errorCode) {
      super(errorCode);
    }
}
