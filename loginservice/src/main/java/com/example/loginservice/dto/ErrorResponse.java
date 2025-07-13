package com.example.loginservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponse {
    @JsonProperty("message")
    private String message;
    
    // Default constructor
    public ErrorResponse() {}
    
    // Constructor with message
    public ErrorResponse(String message) {
        this.message = message;
    }
    
    // Getter method
    public String getMessage() {
        return message;
    }
    
    // Setter method
    public void setMessage(String message) {
        this.message = message;
    }
}
