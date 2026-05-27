package com.example.petapp.application.common.exception;

import lombok.Getter;

@Getter
public class PetCommunityException extends RuntimeException {

    private final ErrorCode errorCode;

    public PetCommunityException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
