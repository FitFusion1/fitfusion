package com.fitfusion.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class AuthController {

    @GetMapping("/register")
    public String registerForm() {
        return "/user/register";
    }

    @PostMapping("/register")
    public String registerUser() {

        return null;
    }
}
