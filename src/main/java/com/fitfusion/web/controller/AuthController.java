package com.fitfusion.web.controller;

import com.fitfusion.exception.UserRegisterException;
import com.fitfusion.service.UserService;
import com.fitfusion.vo.User;
import com.fitfusion.web.form.UserRegisterForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.util.Date;
import java.util.Map;

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
    public String registerUser(@Valid @ModelAttribute("registerForm") UserRegisterForm form,
                               BindingResult errors,
                               RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            return "user/register";
        }
        try {
            User user = userService.registerUser(form);
            redirectAttributes.addFlashAttribute("user", user);
        } catch (UserRegisterException ex) {
            String field = ex.getField();
            String message = ex.getMessage();
            errors.rejectValue(field, "11", message);

            return "user/register";
        }

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

}
