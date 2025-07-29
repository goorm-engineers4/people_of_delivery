package com.example.cloudfour.peopleofdelivery.domain.order.exception;

import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.code.BaseErrorCode;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.exception.CustomException;

public class OrderItemException extends CustomException {
    public OrderItemException(BaseErrorCode errorCode) {
      super(errorCode);
    }
}
