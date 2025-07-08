package com.fitfusion.web.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registration")
public class ApiRegistrationController {

    @GetMapping("/check-username")
    public ResponseEntity<Void> checkUsername(@RequestParam String username) {

        return ResponseEntity.ok().build();
    }
}
