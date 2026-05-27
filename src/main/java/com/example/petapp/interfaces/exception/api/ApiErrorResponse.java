package com.example.petapp.interfaces.exception.api;

import com.example.petapp.application.common.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.Map;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    private final String code;
    private final String message;
    private final Map<String, String> errors;

    private ApiErrorResponse(String code, String message, Map<String, String> errors) {
        this.code = code;
        this.message = message;
        this.errors = errors;
    }

    public static ApiErrorResponse of(String code, String message) {
        return new ApiErrorResponse(code, message, null);
    }

    public static ApiErrorResponse validation(Map<String, String> errors) {
        return new ApiErrorResponse(ErrorCode.VALIDATION_ERROR.getCode(), "입력값이 올바르지 않습니다.", errors);
    }
}
