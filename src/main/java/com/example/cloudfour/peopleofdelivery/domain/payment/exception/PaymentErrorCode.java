package com.example.cloudfour.peopleofdelivery.domain.payment.exception;

import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PaymentErrorCode implements BaseErrorCode {
    CREATE_FAILED(HttpStatus.BAD_REQUEST, "PAYMENT400_1", "결제 정보를 생성할 수 없습니다."),
    UPDATE_FAILED(HttpStatus.BAD_REQUEST, "PAYMENT400_2", "결제 정보를 수정할 수 없습니다."),
    DELETE_FAILED(HttpStatus.BAD_REQUEST, "PAYMENT400_3", "결제 정보를 삭제할 수 없습니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "PAYMENT401", "결제에 접근할 수 있는 권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT404", "결제를 찾을 수 없습니다."),
    ALREADY_ADD(HttpStatus.CONFLICT, "PAYMENT409", "이미 등록된 결제입니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PAYMENT500", "결제 처리 중 서버 오류가 발생했습니다."),

    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT404", "결제 정보를 찾을 수 없습니다"),
    PAYMENT_ALREADY_APPROVED(HttpStatus.CONFLICT, "PAYMENT409", "이미 승인된 결제입니다"),
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "PAYMENT400", "결제에 실패했습니다"),
    INVALID_PAYMENT_STATUS(HttpStatus.BAD_REQUEST, "PAYMENT400_1", "유효하지 않은 결제 상태입니다"),
    TOSS_ERROR(HttpStatus.BAD_REQUEST, "TOSS_400", "Toss 결제 오류 발생"),
    DUPLICATE_PAYMENT(HttpStatus.CONFLICT, "PAYMENT_003", "이미 승인된 결제입니다."),
    UNAUTHORIZED_PAYMENT_ACCESS(HttpStatus.FORBIDDEN, "PAYMENT_004", "결제에 접근할 권한이 없습니다."),
    TOSS_STATUS_UNKNOWN(HttpStatus.INTERNAL_SERVER_ERROR, "PAYMENT_005", "알 수 없는 Toss 결제 상태입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    PaymentErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
