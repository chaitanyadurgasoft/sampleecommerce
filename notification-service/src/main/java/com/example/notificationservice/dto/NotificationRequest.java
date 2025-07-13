package com.example.notificationservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationRequest {
    @JsonProperty("userId")
    private String userId;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    // Default constructor
    public NotificationRequest() {}
    
    // Constructor with parameters
    public NotificationRequest(String userId, String message, String type, String timestamp) {
        this.userId = userId;
        this.message = message;
        this.type = type;
        this.timestamp = timestamp;
    }
    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
