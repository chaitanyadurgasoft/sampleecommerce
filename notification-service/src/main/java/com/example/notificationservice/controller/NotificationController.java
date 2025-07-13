package com.example.notificationservice.controller;

import com.example.notificationservice.dto.NotificationRequest;
import com.example.notificationservice.dto.NotificationResponse;
import com.example.notificationservice.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class NotificationController {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    
    @Autowired
    private NotificationService notificationService;
    
    @PostMapping("/notify")
    public ResponseEntity<NotificationResponse> sendNotification(@RequestBody NotificationRequest request) {
        logger.info("Received notification request for user: {}", request.getUserId());
        
        // Validate request
        if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
            NotificationResponse errorResponse = new NotificationResponse(
                false, 
                "UserId is required", 
                null, 
                java.time.LocalDateTime.now().toString()
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            NotificationResponse errorResponse = new NotificationResponse(
                false, 
                "Message is required", 
                null, 
                java.time.LocalDateTime.now().toString()
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        NotificationResponse response = notificationService.sendNotification(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/notifications/{userId}")
    public ResponseEntity<List<NotificationRequest>> getUserNotifications(@PathVariable String userId) {
        logger.info("Fetching notifications for user: {}", userId);
        List<NotificationRequest> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Notification service is running!");
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Object> getStats() {
        return ResponseEntity.ok(new Object() {
            public final String service = "notification-service";
            public final String status = "healthy";
            public final int totalNotifications = notificationService.getTotalNotificationCount();
            public final String timestamp = java.time.LocalDateTime.now().toString();
        });
    }
}	
