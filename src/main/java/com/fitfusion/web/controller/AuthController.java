package com.fitfusion.web.controller;

import com.fitfusion.web.form.UserRegisterForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/user")
public class AuthController {

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerForm", new UserRegisterForm());
        model.addAttribute("currentDate", new Date());
        return "user/register";
    }

    @PostMapping("/register")
    public String registerUser() {
        return "user/register-complete";
    }
}
