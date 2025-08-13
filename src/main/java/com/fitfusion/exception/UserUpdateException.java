package com.fitfusion.exception;

import lombok.Getter;

@Getter
public class UserUpdateException extends AppException {

    private final String field;

    public UserUpdateException(String field, String message) {
        super(message);
        this.field = field;
    }

}
