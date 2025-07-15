package com.fitfusion.web.rest;

import com.fitfusion.service.UserService;
import com.fitfusion.web.response.ApiResponse;
import com.fitfusion.web.response.ResponseEntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registration")
public class ApiRegistrationController {

    @Autowired
    private UserService userService;

    /*
     * 요청 방식 : GET
     * 요청 URI : /api/registration/validation
     * 옵션 : ?type=username, value=xxx
     *       ?type=email, value=xxx
     */
    @GetMapping("/validation")
    public ResponseEntity<ApiResponse<Boolean>> checkDuplicate(
            @RequestParam("type") String type,
            @RequestParam("value") String value) {
        Boolean exists = userService.checkExists(type, value);

        return ResponseEntityUtils.ok(exists);
    }
}
