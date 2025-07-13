package com.example.loginservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse {
    @JsonProperty("token")
    private String token;
    
    // Default constructor
    public LoginResponse() {}
    
    // Constructor with token
    public LoginResponse(String token) {
        this.token = token;
    }
    
    // Getter method
    public String getToken() {
        return token;
    }
    
    // Setter method
    public void setToken(String token) {
        this.token = token;
    }
}
