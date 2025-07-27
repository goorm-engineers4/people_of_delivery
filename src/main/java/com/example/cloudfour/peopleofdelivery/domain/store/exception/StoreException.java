package com.example.cloudfour.peopleofdelivery.domain.store.exception;

import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.code.BaseErrorCode;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.exception.CustomException;

public class StoreException extends CustomException {
    public StoreException(BaseErrorCode errorCode) {
      super(errorCode);
    }
}
