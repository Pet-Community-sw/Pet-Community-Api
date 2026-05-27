package com.example.petapp.interfaces.exception.stomp;

import com.example.petapp.application.common.exception.ErrorCode;
import com.example.petapp.application.common.exception.PetCommunityException;
import lombok.Getter;

@Getter
public class StompErrorResponse {

    private final String code;
    private final String message;

    private StompErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static StompErrorResponse from(Throwable ex) {
        if (ex instanceof PetCommunityException petCommunityException) {
            ErrorCode errorCode = petCommunityException.getErrorCode();
            return new StompErrorResponse(errorCode.getCode(), petCommunityException.getMessage());
        }
        if (ex instanceof IllegalArgumentException) {
            return new StompErrorResponse(ErrorCode.BAD_REQUEST.getCode(), ex.getMessage());
        }
        return new StompErrorResponse(ErrorCode.INTERNAL_ERROR.getCode(), "웹소켓 처리 중 오류가 발생했습니다.");
    }
}
