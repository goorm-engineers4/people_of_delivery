package com.example.cloudfour.peopleofdelivery.global.ai.exception;

import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.code.BaseErrorCode;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.exception.CustomException;

public class AiLogException extends CustomException {
    public AiLogException(BaseErrorCode errorCode) {
      super(errorCode);
    }
}
