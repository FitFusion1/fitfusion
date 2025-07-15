package com.fitfusion.web.rest;

import com.fitfusion.web.request.SavedRequestWrapper;
import com.fitfusion.web.response.ApiResponse;
import com.fitfusion.web.response.ResponseEntityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiRedirectController {

    @PostMapping("/save-redirect-url")
    public ResponseEntity<ApiResponse<Void>> saveRedirectUrl(@RequestBody Map<String, String> payload,
                                                             HttpServletRequest request) {
        String redirectUrl = payload.get("redirectUrl");

        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            HttpServletRequest dummyRequest = new SavedRequestWrapper(request, redirectUrl);

            DefaultSavedRequest savedRequest = new DefaultSavedRequest(dummyRequest, new PortResolverImpl());

            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_SAVED_REQUEST", savedRequest);
        }
        return ResponseEntityUtils.ok(redirectUrl);
    }

    @GetMapping("/get-redirect-url")
    public ResponseEntity<ApiResponse<String>> getRedirectUrl(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntityUtils.fail(404, "No session found.");
        }

        Object savedRequest = session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");

        if (savedRequest instanceof SavedRequest saved) {
            return ResponseEntityUtils.ok("Saved redirect URL found", saved.getRedirectUrl());
        }

        return ResponseEntityUtils.fail(404, "No redirect URL saved.");
    }
}
