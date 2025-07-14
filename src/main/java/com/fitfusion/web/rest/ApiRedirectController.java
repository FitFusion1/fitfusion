package com.fitfusion.web.rest;

import com.fitfusion.web.response.ApiResponse;
import com.fitfusion.web.response.ResponseEntityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiRedirectController {

    @PostMapping("/save-request-url")
    public ResponseEntity<ApiResponse<Void>> saveRequestedUrl(@RequestBody Map<String, String> payload,
                                                        HttpServletRequest request) {
        String redirectUrl = payload.get("redirectUrl");

        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            request.getSession().setAttribute("SPRING_SECURITY_SAVED_REQUEST", redirectUrl);
            return ResponseEntityUtils.ok();
        } else {
            return ResponseEntityUtils.fail(500, "URL 저장 오류");
        }

    }


}
