package com.fitfusion.web.advice;

import com.fitfusion.exception.AppException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(basePackages = "com.fitfusion.web.controller")
public class ControllerExceptionHandler {

    @ExceptionHandler(AppException.class)
    public String handleAppException(AppException ex, Model model) {
        model.addAttribute("errorCode", 400);
        model.addAttribute("errorTitle", "오류 발생");
        model.addAttribute("errorMessage", ex.getMessage());

        return "error/error";
    }
}
