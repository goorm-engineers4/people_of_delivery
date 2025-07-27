package com.example.cloudfour.peopleofdelivery.domain.order.exception;

import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.code.BaseErrorCode;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.exception.CustomException;

public class OrderException extends CustomException {
    public OrderException(BaseErrorCode errorCode) {
      super(errorCode);
    }
}
