package com.example.loginservice.service;

import com.example.loginservice.dto.RegisterRequest;
import com.example.loginservice.dto.RegisterResponse;
import com.example.loginservice.entity.User;
import com.example.loginservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordService passwordService;
    
    /**
     * Register a new user
     */
    public RegisterResponse registerUser(RegisterRequest request) {
        try {
            // Validate request
            String validationError = validateRegistrationRequest(request);
            if (validationError != null) {
                return new RegisterResponse(false, validationError);
            }
            
            // Check if username already exists
            if (userRepository.existsByUsername(request.getUsername())) {
                return new RegisterResponse(false, "Username already exists");
            }
            
            // Check if email already exists
            Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
            if (existingUser.isPresent()) {
                return new RegisterResponse(false, "Email address already registered");
            }
            
            // Create new user
            User newUser = new User();
            newUser.setUsername(request.getUsername());
            newUser.setEmail(request.getEmail());
            newUser.setPassword(passwordService.encryptPassword(request.getPassword()));
            newUser.setCreatedAt(LocalDateTime.now());
            
            // Save user to database
            User savedUser = userRepository.save(newUser);
            
            logger.info("New user registered successfully: {} with ID: {}", savedUser.getUsername(), savedUser.getId());
            
            return new RegisterResponse(
                true, 
                "User registered successfully", 
                savedUser.getId(), 
                savedUser.getUsername(), 
                savedUser.getEmail()
            );
            
        } catch (Exception e) {
            logger.error("Error during user registration: {}", e.getMessage());
            return new RegisterResponse(false, "Registration failed: " + e.getMessage());
        }
    }
    
    /**
     * Authenticate user login
     */
    public Optional<User> authenticateUser(String username, String password) {
        try {
            Optional<User> userOpt = userRepository.findByUsername(username);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                // Verify password
                if (passwordService.verifyPassword(password, user.getPassword())) {
                    // Update last login time
                    user.setLastLogin(LocalDateTime.now());
                    userRepository.save(user);
                    
                    logger.info("User authenticated successfully: {}", username);
                    return Optional.of(user);
                } else {
                    logger.warn("Invalid password for user: {}", username);
                }
            } else {
                logger.warn("User not found: {}", username);
            }
            
            return Optional.empty();
            
        } catch (Exception e) {
            logger.error("Error during authentication: {}", e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Get all users (admin function)
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Get user by ID
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Get user by username
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Delete user by ID
     */
    public boolean deleteUser(Long id) {
        try {
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id);
                logger.info("User deleted with ID: {}", id);
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("Error deleting user {}: {}", id, e.getMessage());
            return false;
        }
    }
    
    /**
     * Get user statistics
     */
    public Map<String, Object> getUserStatistics() {
        try {
            long totalUsers = userRepository.count();
            long recentUsers = userRepository.findRecentlyActiveUsers(
                LocalDateTime.now().minusDays(7)
            ).size();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalUsers", totalUsers);
            stats.put("recentlyActiveUsers", recentUsers);
            stats.put("timestamp", LocalDateTime.now().toString());
            
            return stats;
        } catch (Exception e) {
            logger.error("Error fetching user statistics: {}", e.getMessage());
            Map<String, Object> errorStats = new HashMap<>();
            errorStats.put("error", "Unable to fetch statistics");
            return errorStats;
        }
    }
    
    /**
     * Validate registration request
     */
    private String validateRegistrationRequest(RegisterRequest request) {
        // Check if passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return "Passwords do not match";
        }
        
        // Check password strength
        if (!passwordService.isValidPassword(request.getPassword())) {
            return "Password must be at least 6 characters and contain both letters and numbers";
        }
        
        // Check username format
        if (!isValidUsername(request.getUsername())) {
            return "Username can only contain letters, numbers, and underscores";
        }
        
        return null; // No validation errors
    }
    
    /**
     * Check if username format is valid
     */
    private boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9_]+$");
    }
}
