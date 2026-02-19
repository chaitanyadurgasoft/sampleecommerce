package com.example.loginservice.controller;

import com.example.loginservice.dto.*;
import com.example.loginservice.entity.User;
import com.example.loginservice.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    
    @Autowired
    private UserService userService;

    /**
     * User Registration Endpoint
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest, 
                                                   BindingResult bindingResult) {
        logger.info("Registration attempt for username: {}", registerRequest.getUsername());
        
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
            
            RegisterResponse errorResponse = new RegisterResponse(false, "Validation error: " + errorMessage);
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        // Attempt to register user
        RegisterResponse response = userService.registerUser(registerRequest);
        
        if (response.isSuccess()) {
            logger.info("User registered successfully: {}", registerRequest.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            logger.warn("Registration failed for username: {} - {}", registerRequest.getUsername(), response.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * User Login Endpoint (Updated)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        logger.info("Login attempt for username: {}", loginRequest.getUsername());
        
        try {
            // Authenticate user
            Optional<User> userOpt = userService.authenticateUser(
                loginRequest.getUsername(), 
                loginRequest.getPassword()
            );
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                logger.info("Login successful for user: {}", loginRequest.getUsername());
                
                // Generate JWT token (simple version)
                String token = "jwt-token-" + user.getId() + "-" + System.currentTimeMillis();
                LoginResponse response = new LoginResponse(token);
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Login failed for username: {}", loginRequest.getUsername());
                ErrorResponse error = new ErrorResponse("Invalid username or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
        } catch (Exception e) {
            logger.error("Error during login: {}", e.getMessage());
            ErrorResponse error = new ErrorResponse("Login service temporarily unavailable");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Get all users (Admin endpoint)
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error fetching users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get user by ID
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        try {
            Optional<User> userOpt = userService.getUserById(id);
            if (userOpt.isPresent()) {
                return ResponseEntity.ok(userOpt.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error fetching user {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Delete user (Admin endpoint)
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        try {
            boolean deleted = userService.deleteUser(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", deleted);
            response.put("message", deleted ? "User deleted successfully" : "User not found");
            
            if (deleted) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error deleting user {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get user statistics
     */
    @GetMapping("/users/stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        try {
            Object stats = userService.getUserStatistics();
            
            // Convert to Map to avoid self-reference issues
            Map<String, Object> statsMap = new HashMap<>();
            if (stats != null) {
                // If stats is already a Map, cast it; otherwise create a simple map
                if (stats instanceof Map) {
                    statsMap = (Map<String, Object>) stats;
                } else {
                    // Create basic stats map
                    statsMap.put("status", "success");
                    statsMap.put("timestamp", java.time.LocalDateTime.now().toString());
                }
            }
            
            return ResponseEntity.ok(statsMap);
        } catch (Exception e) {
            logger.error("Error fetching user stats: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unable to fetch statistics");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Check if username is available
     */
    @GetMapping("/check-username/{username}")
    public ResponseEntity<Map<String, Object>> checkUsername(@PathVariable String username) {
        try {
            Optional<User> userOpt = userService.getUserByUsername(username);
            boolean available = userOpt.isEmpty();
            
            Map<String, Object> response = new HashMap<>();
            response.put("available", available);
            response.put("username", username);
            response.put("message", available ? "Username is available" : "Username is already taken");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error checking username: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Login service with registration is running!");
    }
}
