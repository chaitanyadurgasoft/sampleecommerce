package com.example.catalogservice.controller;

import com.example.catalogservice.dto.BuyRequest;
import com.example.catalogservice.dto.BuyResponse;
import com.example.catalogservice.dto.PaymentRequest;
import com.example.catalogservice.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CatalogController {

    private static final Logger logger = LoggerFactory.getLogger(CatalogController.class);
    
    @Value("${payment.service.url:http://payment-service:8080}")
    private String paymentServiceUrl;
    
    private final RestTemplate restTemplate;
    
    public CatalogController() {
        this.restTemplate = new RestTemplate();
    }
    
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts() {
        logger.info("Fetching product catalog");
        
        List<Product> products = Arrays.asList(
            new Product(1L, "Laptop", 999.99, "High-performance laptop for work and gaming", "Electronics", "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=300"),
            new Product(2L, "Smartphone", 699.99, "Latest smartphone with advanced camera", "Electronics", "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=300"),
            new Product(3L, "Headphones", 199.99, "Wireless noise-canceling headphones", "Electronics", "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=300"),
            new Product(4L, "Coffee Maker", 149.99, "Automatic coffee maker with timer", "Home & Kitchen", "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=300"),
            new Product(5L, "Running Shoes", 89.99, "Comfortable running shoes for all terrains", "Sports & Outdoors", "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=300"),
            new Product(6L, "Backpack", 59.99, "Durable travel backpack with multiple compartments", "Travel", "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=300"),
            new Product(7L, "Desk Chair", 249.99, "Ergonomic office chair with lumbar support", "Furniture", "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=300"),
            new Product(8L, "Water Bottle", 24.99, "Insulated stainless steel water bottle", "Sports & Outdoors", "https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=300")
        );
        
        return ResponseEntity.ok(products);
    }
    
    @PostMapping("/buy")
    public ResponseEntity<BuyResponse> buyProduct(@RequestBody BuyRequest buyRequest) {
        logger.info("Processing buy request for user: {} and product: {}", 
                   buyRequest.getUserId(), buyRequest.getProductId());
        
        // Validate input
        if (buyRequest.getUserId() == null || buyRequest.getProductId() == null) {
            BuyResponse errorResponse = new BuyResponse(false, "Invalid request: userId and productId are required", null, null, null);
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        // Find the product
        Optional<Product> productOpt = getProductById(buyRequest.getProductId());
        if (productOpt.isEmpty()) {
            BuyResponse errorResponse = new BuyResponse(false, "Product not found", null, null, null);
            return ResponseEntity.notFound().build();
        }
        
        Product product = productOpt.get();
        
        try {
            // Create payment request
            PaymentRequest paymentRequest = new PaymentRequest(
                buyRequest.getUserId(), 
                buyRequest.getProductId(), 
                product.getPrice(),
                product.getName()
            );
            
            // Call payment service
            logger.info("Calling payment service at: {}/payment", paymentServiceUrl);
            ResponseEntity<String> paymentResponse = restTemplate.postForEntity(
                paymentServiceUrl + "/payment", 
                paymentRequest, 
                String.class
            );
            
            if (paymentResponse.getStatusCode().is2xxSuccessful()) {
                // Payment successful
                String transactionId = "TXN-" + System.currentTimeMillis();
                BuyResponse successResponse = new BuyResponse(
                    true, 
                    "Purchase successful", 
                    transactionId,
                    product.getName(),
                    product.getPrice()
                );
                logger.info("Purchase completed successfully for user: {} and product: {}", 
                           buyRequest.getUserId(), product.getName());
                return ResponseEntity.ok(successResponse);
            } else {
                // Payment failed
                BuyResponse errorResponse = new BuyResponse(false, "Payment failed", null, product.getName(), product.getPrice());
                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(errorResponse);
            }
            
        } catch (RestClientException e) {
            logger.error("Error calling payment service: {}", e.getMessage());
            BuyResponse errorResponse = new BuyResponse(false, "Payment service unavailable", null, product.getName(), product.getPrice());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Catalog service is running!");
    }
    
    private Optional<Product> getProductById(Long productId) {
        return getProducts().getBody().stream()
            .filter(product -> product.getId().equals(productId))
            .findFirst();
    }
}
