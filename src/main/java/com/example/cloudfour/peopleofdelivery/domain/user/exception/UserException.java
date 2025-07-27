package com.example.cloudfour.peopleofdelivery.domain.user.exception;

import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.code.BaseErrorCode;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.exception.CustomException;

public class UserException extends CustomException {
    public UserException(BaseErrorCode errorCode) {
      super(errorCode);
    }
}
