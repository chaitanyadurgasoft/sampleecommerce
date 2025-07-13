package com.example.notificationservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationResponse {
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("notificationId")
    private String notificationId;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    // Default constructor
    public NotificationResponse() {}
    
    // Constructor with parameters
    public NotificationResponse(boolean success, String message, String notificationId, String timestamp) {
        this.success = success;
        this.message = message;
        this.notificationId = notificationId;
        this.timestamp = timestamp;
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getNotificationId() {
        return notificationId;
    }
    
    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
