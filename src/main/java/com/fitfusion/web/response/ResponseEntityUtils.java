package com.fitfusion.web.response;

import org.springframework.http.ResponseEntity;

public class ResponseEntityUtils {

    public static ResponseEntity<ApiResponse<Void>> ok() {
        return ResponseEntity
                .status(200)
                .body(ApiResponse.success());
    }

    public static ResponseEntity<ApiResponse<Void>> ok(String message) {
        return ResponseEntity
                .status(200)
                .body(ApiResponse.success(message));
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return ResponseEntity
                .status(200)
                .body(ApiResponse.success(data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        return ResponseEntity
                .status(200)
                .body(ApiResponse.success(message, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> fail(int status, String message) {
        return ResponseEntity
                .status(status)
                .body(ApiResponse.fail(status, message));
    }
}
