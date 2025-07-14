package com.fitfusion.web.controller;

import com.fitfusion.service.UserService;
import com.fitfusion.vo.User;
import com.fitfusion.web.form.UserRegisterForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;

@Controller
@RequestMapping("/user")
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerForm", new UserRegisterForm());
        model.addAttribute("currentDate", new Date());
        return "user/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("registerForm") UserRegisterForm form,
                               RedirectAttributes redirectAttributes) {
        User user = userService.registerUser(form);
        redirectAttributes.addFlashAttribute("user", user);

        return "redirect:/user/complete";
    }

    @GetMapping("/complete")
    public String completeRegistration() {
        return "user/register-complete";
    }

    @GetMapping("/login")
    public String loginForm() {

        return "user/login";
    }

    @PostMapping("/login")
    public String loginUser() {

        return "redirect:/";
    }
}
