package com.example.cloudfour.peopleofdelivery.domain.order.exception;

import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.code.BaseErrorCode;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.exception.CustomException;

public class OrderHistoryException extends CustomException {
    public OrderHistoryException(BaseErrorCode errorCode) {
      super(errorCode);
    }
}
