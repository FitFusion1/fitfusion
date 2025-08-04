package com.fitfusion.web.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/*
 * HttpStatus 오류 처리를 위한 컨트롤러
 */
@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object statusObject = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = statusObject != null ? Integer.parseInt(statusObject.toString()) : 500;

        HttpStatus status = HttpStatus.resolve(statusCode);
        String title = (status != null) ? status.getReasonPhrase() : "알 수 없는 오류";
        String message = getDefaultMessage(statusCode);

        model.addAttribute("errorCode", statusCode);
        model.addAttribute("errorTitle", title);
        model.addAttribute("errorMessage", message);

        return "error/error";
    }
    
    private String getDefaultMessage(int statusCode) {
        return switch (statusCode) {
            case 401 -> "접근을 위해 로그인이 필요합니다.";
            case 403 -> "이 페이지를 열람할 권한이 부족합니다.";
            case 404 -> "페이지가 존재하지 않습니다.";
            case 500 -> "서버 내부 오류가 발생했습니다.";
            default -> "알 수 없는 오류가 발생했습니다.";
        };
    }
}
