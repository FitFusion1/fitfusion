package com.fitfusion.web.advice;

import com.fitfusion.exception.AppException;
import com.fitfusion.web.response.ApiResponse;
import com.fitfusion.web.response.ResponseEntityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.fitfusion.web.rest")
public class RestControllerExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException ex) {
        return ResponseEntityUtils.fail(500, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        return ResponseEntityUtils.fail(500, "서버 오류가 발생했습니다.");
    }
}
