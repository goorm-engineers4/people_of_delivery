package com.example.cloudfour.peopleofdelivery.domain.cartitem.exception;

import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum CartItemErrorCode implements BaseErrorCode {
    CREATE_FAILED(HttpStatus.BAD_REQUEST, "CARTITEM400_1", "장바구니 아이템 정보를 생성할 수 없습니다."),
    UPDATE_FAILED(HttpStatus.BAD_REQUEST, "CARTITEM400_2", "장바구니 아이템 정보를 수정할 수 없습니다."),
    DELETE_FAILED(HttpStatus.BAD_REQUEST, "CARTITEM400_3", "장바구니 아이템 정보를 삭제할 수 없습니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "CARTITEM401", "장바구니 아이템에 접근할 수 있는 권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "CARTITEM404", "장바구니 아이템을 찾을 수 없습니다."),
    ALREADY_ADD(HttpStatus.CONFLICT, "CARTITEM409", "이미 등록된 장바구니 아이템입니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "CARTITEM500", "장바구니 아이템 처리 중 서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
