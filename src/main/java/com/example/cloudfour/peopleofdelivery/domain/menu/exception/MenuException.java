package com.example.cloudfour.peopleofdelivery.domain.menu.exception;

import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.code.BaseErrorCode;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.exception.CustomException;

public class MenuException extends CustomException {
    public MenuException(BaseErrorCode errorCode) {
      super(errorCode);
    }
}
