package com.example.cloudfour.peopleofdelivery.domain.menu.exception;

import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.code.BaseErrorCode;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.exception.CustomException;

public class MenuCategoryException extends CustomException {
    public MenuCategoryException(BaseErrorCode errorCode) {
      super(errorCode);
    }
}
