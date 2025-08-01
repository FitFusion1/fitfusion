package com.fitfusion.exception;

import lombok.Getter;

@Getter
public class UserRegisterException extends AppException {

    private final String field;

    public UserRegisterException(String field, String message) {
        super(message);
        this.field = field;
    }

}
