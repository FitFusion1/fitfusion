package com.fitfusion.web.controller;

import com.fitfusion.dto.PasswordChangeRequestDto;
import com.fitfusion.exception.UserNotFoundException;
import com.fitfusion.exception.UserUpdateException;
import com.fitfusion.security.SecurityUser;
import com.fitfusion.service.UserService;
import com.fitfusion.vo.User;
import com.fitfusion.web.form.UserEditForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    @GetMapping("/login")
    public String loginForm() {
        return "user/login";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mypage")
    public String myPage() {
        return "user/mypage";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/edit")
    public String editForm(Model model, @AuthenticationPrincipal SecurityUser user) {
        User foundUser = userService.getUserById(user.getUser().getUserId());
        if (foundUser == null) {
            throw new UserNotFoundException("UserNotFound", "해당 유저를 찾을 수 없습니다.");
        }
        model.addAttribute("userEditForm", userService.getUserEditForm(foundUser));
        model.addAttribute("passwordForm", new PasswordChangeRequestDto());
        return "user/edit-user";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/edit")
    public String editUserDetails(@Valid @ModelAttribute("userEditForm") UserEditForm form,
                                  BindingResult errors,
                                  Model model,
                                  @AuthenticationPrincipal SecurityUser user) {
        if (errors.hasErrors()) {
            return "user/edit-user";
        }
        try {
            UserEditForm updatedUserForm = userService.updateUserInfo(form);
            updatedUserForm.setUserId(user.getUser().getUserId());
            model.addAttribute("userEditForm", updatedUserForm);
        } catch (UserUpdateException ex) {
            String field = ex.getField();
            String message = ex.getMessage();
            errors.rejectValue(field, "500", message);

            return "user/edit-user";
        }
        return "redirect:/user/edit";
    }
}
