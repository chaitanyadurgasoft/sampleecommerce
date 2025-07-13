package com.example.catalogservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BuyRequest {
    @JsonProperty("userId")
    private String userId;
    
    @JsonProperty("productId")
    private Long productId;
    
    // Default constructor
    public BuyRequest() {}
    
    // Constructor with parameters
    public BuyRequest(String userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
    }
    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
