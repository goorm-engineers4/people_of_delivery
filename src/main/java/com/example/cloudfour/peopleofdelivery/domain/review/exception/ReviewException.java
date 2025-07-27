package com.example.cloudfour.peopleofdelivery.domain.review.exception;

import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.code.BaseErrorCode;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.exception.CustomException;

public class ReviewException extends CustomException {
    public ReviewException(BaseErrorCode errorCode) {
      super(errorCode);
    }
}
