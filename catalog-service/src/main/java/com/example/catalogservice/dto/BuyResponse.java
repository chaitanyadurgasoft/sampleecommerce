package com.example.catalogservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BuyResponse {
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("transactionId")
    private String transactionId;
    
    @JsonProperty("productName")
    private String productName;
    
    @JsonProperty("amount")
    private Double amount;
    
    // Default constructor
    public BuyResponse() {}
    
    // Constructor with parameters
    public BuyResponse(boolean success, String message, String transactionId, String productName, Double amount) {
        this.success = success;
        this.message = message;
        this.transactionId = transactionId;
        this.productName = productName;
        this.amount = amount;
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
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public Double getAmount() {
        return amount;
    }
    
    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
