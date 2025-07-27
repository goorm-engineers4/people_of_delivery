package com.example.cloudfour.peopleofdelivery.domain.cart.exception;

import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.code.BaseErrorCode;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.exception.CustomException;

public class CartException extends CustomException {
    public CartException(BaseErrorCode errorCode) {
      super(errorCode);
    }
}
