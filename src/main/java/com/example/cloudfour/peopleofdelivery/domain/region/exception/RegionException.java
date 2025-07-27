package com.example.cloudfour.peopleofdelivery.domain.region.exception;

import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.code.BaseErrorCode;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.exception.CustomException;

public class RegionException extends CustomException {
    public RegionException(BaseErrorCode errorCode) {
      super(errorCode);
    }
}
