package com.example.loginservice.controller;

import com.example.loginservice.dto.LoginRequest;
import com.example.loginservice.dto.LoginResponse;
import com.example.loginservice.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allow frontend to access this API
public class LoginController {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // Check if username and password match our hardcoded values
        if ("admin".equals(loginRequest.getUsername()) && 
            "password".equals(loginRequest.getPassword())) {
            
            // Return success response with token
            LoginResponse response = new LoginResponse("dummy-token-123");
            return ResponseEntity.ok(response);
        } else {
            // Return error response for invalid credentials
            ErrorResponse error = new ErrorResponse("Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
    
    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Login service is running!");
    }
}
