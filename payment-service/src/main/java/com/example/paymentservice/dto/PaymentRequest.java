package com.example.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentRequest {
    @JsonProperty("userId")
    private String userId;
    
    @JsonProperty("productId")
    private Long productId;
    
    @JsonProperty("amount")
    private Double amount;
    
    @JsonProperty("productName")
    private String productName;
    
    // Default constructor
    public PaymentRequest() {}
    
    // Constructor with parameters
    public PaymentRequest(String userId, Long productId, Double amount, String productName) {
        this.userId = userId;
        this.productId = productId;
        this.amount = amount;
        this.productName = productName;
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
    
    public Double getAmount() {
        return amount;
    }
    
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
}
