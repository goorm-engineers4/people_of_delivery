package com.example.cloudfour.peopleofdelivery.domain.store.exception;

import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.code.BaseErrorCode;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.exception.CustomException;

public class StoreCategoryException extends CustomException {
    public StoreCategoryException(BaseErrorCode errorCode) {
      super(errorCode);
    }
}
