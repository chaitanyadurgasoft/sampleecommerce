package com.example.paymentservice.controller;

import com.example.paymentservice.dto.PaymentRequest;
import com.example.paymentservice.dto.PaymentResponse;
import com.example.paymentservice.dto.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final Random random = new Random();
    private final RestTemplate restTemplate;
    
    @Value("${notification.service.url:http://notification-service:8080}")
    private String notificationServiceUrl;
    
    public PaymentController() {
        this.restTemplate = new RestTemplate();
    }
    
    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest paymentRequest) {
        logger.info("Processing payment for user: {} and product: {}", 
                   paymentRequest.getUserId(), paymentRequest.getProductId());
        
        // Validate input
        if (paymentRequest.getUserId() == null || paymentRequest.getProductId() == null) {
            PaymentResponse errorResponse = new PaymentResponse(
                false, 
                "Invalid request: userId and productId are required", 
                null, 
                paymentRequest.getUserId(), 
                paymentRequest.getProductId(), 
                paymentRequest.getAmount(),
                getCurrentTimestamp()
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        // Generate transaction ID
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // Simulate payment processing delay
        try {
            Thread.sleep(1000 + random.nextInt(2000)); // 1-3 seconds delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Randomly simulate payment success/failure (70% success rate)
        boolean paymentSuccess = random.nextDouble() < 0.7;
        
        String message;
        String notificationType;
        if (paymentSuccess) {
            message = String.format("Payment successful for %s. Amount: $%.2f. Transaction ID: %s", 
                                   paymentRequest.getProductName() != null ? paymentRequest.getProductName() : "Product #" + paymentRequest.getProductId(),
                                   paymentRequest.getAmount() != null ? paymentRequest.getAmount() : 0.0,
                                   transactionId);
            notificationType = "payment_success";
        } else {
            message = String.format("Payment failed for %s. Please try again or use a different payment method.", 
                                   paymentRequest.getProductName() != null ? paymentRequest.getProductName() : "Product #" + paymentRequest.getProductId());
            notificationType = "payment_failure";
        }
        
        // Send notification to notification service
        sendNotification(paymentRequest.getUserId(), message, notificationType);
        
        // Create payment response
        PaymentResponse response = new PaymentResponse(
            paymentSuccess,
            message,
            paymentSuccess ? transactionId : null,
            paymentRequest.getUserId(),
            paymentRequest.getProductId(),
            paymentRequest.getAmount(),
            getCurrentTimestamp()
        );
        
        logger.info("Payment {} for user: {} - Transaction ID: {}", 
                   paymentSuccess ? "successful" : "failed", 
                   paymentRequest.getUserId(), 
                   transactionId);
        
        return paymentSuccess ? 
            ResponseEntity.ok(response) : 
            ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(response);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Payment service is running!");
    }
    
    @GetMapping("/status")
    public ResponseEntity<Object> status() {
        return ResponseEntity.ok(new Object() {
            public final String service = "payment-service";
            public final String status = "healthy";
            public final String timestamp = getCurrentTimestamp();
            public final String notificationServiceUrl = PaymentController.this.notificationServiceUrl;
        });
    }
    
    private void sendNotification(String userId, String message, String type) {
        try {
            NotificationRequest notificationRequest = new NotificationRequest(
                userId, 
                message, 
                type, 
                getCurrentTimestamp()
            );
            
            logger.info("Sending notification to: {}/notify", notificationServiceUrl);
            ResponseEntity<String> response = restTemplate.postForEntity(
                notificationServiceUrl + "/notify", 
                notificationRequest, 
                String.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Notification sent successfully to user: {}", userId);
            } else {
                logger.warn("Notification service responded with status: {}", response.getStatusCode());
            }
            
        } catch (RestClientException e) {
            logger.error("Failed to send notification to user: {} - Error: {}", userId, e.getMessage());
            // Don't fail the payment if notification fails
        }
    }
    
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
