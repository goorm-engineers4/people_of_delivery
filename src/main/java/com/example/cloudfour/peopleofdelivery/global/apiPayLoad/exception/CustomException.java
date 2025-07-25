package com.example.cloudfour.peopleofdelivery.global.apiPayLoad.exception;

import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {
    private final BaseErrorCode code;

    @Override
    public String getMessage() {
        return code.getMessage();
    }
}
