package com.example.cloudfour.peopleofdelivery.domain.cartitem.exception;

import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.code.BaseErrorCode;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.exception.CustomException;

public class CartItemException extends CustomException {
    public CartItemException(BaseErrorCode errorCode) {
      super(errorCode);
    }
}
