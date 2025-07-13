package com.example.notificationservice.service;

import com.example.notificationservice.dto.NotificationRequest;
import com.example.notificationservice.dto.NotificationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;

@Service
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    // In-memory storage for notifications (in production, use a database)
    private final ConcurrentHashMap<String, List<NotificationRequest>> userNotifications = new ConcurrentHashMap<>();
    
    public NotificationResponse sendNotification(NotificationRequest request) {
        try {
            // Generate notification ID
            String notificationId = "NOTIF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String timestamp = getCurrentTimestamp();
            
            // Set timestamp if not provided
            if (request.getTimestamp() == null || request.getTimestamp().isEmpty()) {
                request.setTimestamp(timestamp);
            }
            
            // Store notification for user
            userNotifications.computeIfAbsent(request.getUserId(), k -> new ArrayList<>()).add(request);
            
            // Print notification to console (simulated notification)
            printNotification(request, notificationId);
            
            // Create success response
            NotificationResponse response = new NotificationResponse(
                true,
                "Notification sent successfully",
                notificationId,
                timestamp
            );
            
            logger.info("Notification sent successfully - ID: {}, User: {}", notificationId, request.getUserId());
            return response;
            
        } catch (Exception e) {
            logger.error("Failed to send notification to user: {} - Error: {}", request.getUserId(), e.getMessage());
            
            return new NotificationResponse(
                false,
                "Failed to send notification: " + e.getMessage(),
                null,
                getCurrentTimestamp()
            );
        }
    }
    
    public List<NotificationRequest> getUserNotifications(String userId) {
        return userNotifications.getOrDefault(userId, new ArrayList<>());
    }
    
    public int getTotalNotificationCount() {
        return userNotifications.values().stream()
                .mapToInt(List::size)
                .sum();
    }
    
    private void printNotification(NotificationRequest request, String notificationId) {
        String border = "=".repeat(80);
        String innerBorder = "-".repeat(78);
        
        System.out.println();
        System.out.println(border);
        System.out.println("📧 NOTIFICATION SERVICE - MESSAGE DELIVERED");
        System.out.println(innerBorder);
        System.out.printf("🆔 Notification ID: %s%n", notificationId);
        System.out.printf("👤 User ID: %s%n", request.getUserId());
        System.out.printf("📝 Type: %s%n", request.getType() != null ? request.getType() : "general");
        System.out.printf("🕒 Timestamp: %s%n", request.getTimestamp());
        System.out.println(innerBorder);
        System.out.printf("💬 Message: %s%n", request.getMessage());
        System.out.println(border);
        System.out.println();
    }
    
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
