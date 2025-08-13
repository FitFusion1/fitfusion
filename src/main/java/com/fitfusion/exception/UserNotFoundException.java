package com.fitfusion.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends AppException {

    private final String field;

    public UserNotFoundException(String field, String message) {
        super(message);
        this.field = field;
    }
}
