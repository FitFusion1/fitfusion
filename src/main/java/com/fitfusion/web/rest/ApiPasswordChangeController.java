package com.fitfusion.web.rest;

import com.fitfusion.dto.PasswordChangeRequestDto;
import com.fitfusion.security.SecurityUser;
import com.fitfusion.service.UserService;
import com.fitfusion.web.response.ApiResponse;
import com.fitfusion.web.response.ResponseEntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/password")
public class ApiPasswordChangeController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<Boolean>> changePassword(
            @AuthenticationPrincipal SecurityUser user,
            @ModelAttribute("passwordForm")PasswordChangeRequestDto request
            ) {
        String currentPassword = request.getCurrentPassword();
        String newPassword = request.getNewPassword();
        String confirmNewPassword = request.getConfirmNewPassword();

        if (!userService.validateExistingPassword(currentPassword, user.getUser().getUserId())) {
            return ResponseEntityUtils.fail(551, "비밀번호 불일치");
        }

        if (!newPassword.equals(confirmNewPassword)) {
            return ResponseEntityUtils.fail(552, "새로운 비밀번호 불일치");
        }

        if (newPassword.length() < 6) {
            return ResponseEntityUtils.fail(553, "6글자 미만 비밀번호");
        }

        userService.updatePassword(newPassword, user.getUser().getUserId());
        return ResponseEntityUtils.ok("비밀번호 변경 성공", true);
    }
}
