package com.example.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentResponse {
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("transactionId")
    private String transactionId;
    
    @JsonProperty("userId")
    private String userId;
    
    @JsonProperty("productId")
    private Long productId;
    
    @JsonProperty("amount")
    private Double amount;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    // Default constructor
    public PaymentResponse() {}
    
    // Constructor with parameters
    public PaymentResponse(boolean success, String message, String transactionId, String userId, Long productId, Double amount, String timestamp) {
        this.success = success;
        this.message = message;
        this.transactionId = transactionId;
        this.userId = userId;
        this.productId = productId;
        this.amount = amount;
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
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
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
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
