package com.example.cloudfour.peopleofdelivery.domain.useraddress.exception;

import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.code.BaseErrorCode;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.exception.CustomException;

public class UserAddressException extends CustomException {
    public UserAddressException(BaseErrorCode errorCode) {
      super(errorCode);
    }
}
