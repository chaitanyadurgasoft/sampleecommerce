package com.example.catalogservice.controller;

import com.example.catalogservice.dto.BuyRequest;
import com.example.catalogservice.dto.BuyResponse;
import com.example.catalogservice.dto.PaymentRequest;
import com.example.catalogservice.entity.Product;
import com.example.catalogservice.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CatalogController {

    private static final Logger logger = LoggerFactory.getLogger(CatalogController.class);
    
    @Value("${payment.service.url:http://payment-service:8082}")
    private String paymentServiceUrl;
    
    @Autowired
    private ProductRepository productRepository;
    
    private final RestTemplate restTemplate;
    
    public CatalogController() {
        this.restTemplate = new RestTemplate();
    }
    
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts() {
        logger.info("Fetching product catalog from database");
        
        try {
            List<Product> products = productRepository.findAll();
            logger.info("Retrieved {} products from database", products.size());
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            logger.error("Error fetching products: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        logger.info("Fetching product with ID: {}", id);
        
        try {
            Optional<Product> productOpt = productRepository.findById(id);
            if (productOpt.isPresent()) {
                return ResponseEntity.ok(productOpt.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error fetching product {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/products/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        logger.info("Fetching products for category: {}", category);
        
        try {
            List<Product> products = productRepository.findByCategory(category);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            logger.error("Error fetching products by category: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        logger.info("Fetching all product categories");
        
        try {
            List<String> categories = productRepository.findAllCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            logger.error("Error fetching categories: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
        
        try {
            // Find the product in database
            Optional<Product> productOpt = productRepository.findById(buyRequest.getProductId());
            if (productOpt.isEmpty()) {
                BuyResponse errorResponse = new BuyResponse(false, "Product not found", null, null, null);
                return ResponseEntity.notFound().build();
            }
            
            Product product = productOpt.get();
            
            // Check stock availability
            if (product.getStockQuantity() <= 0) {
                BuyResponse errorResponse = new BuyResponse(false, "Product out of stock", null, product.getName(), product.getPrice().doubleValue());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }
            
            // Create payment request
            PaymentRequest paymentRequest = new PaymentRequest(
                buyRequest.getUserId(), 
                buyRequest.getProductId(), 
                product.getPrice().doubleValue(),
                product.getName()
            );
            
            // Call payment service
            logger.info("Calling payment service at: {}/api/payment", paymentServiceUrl);
            ResponseEntity<String> paymentResponse = restTemplate.postForEntity(
                paymentServiceUrl + "/api/payment", 
                paymentRequest, 
                String.class
            );
            
            if (paymentResponse.getStatusCode().is2xxSuccessful()) {
                // Payment successful - update stock
                product.setStockQuantity(product.getStockQuantity() - 1);
                productRepository.save(product);
                
                String transactionId = "TXN-" + System.currentTimeMillis();
                BuyResponse successResponse = new BuyResponse(
                    true, 
                    "Purchase successful", 
                    transactionId,
                    product.getName(),
                    product.getPrice().doubleValue()
                );
                logger.info("Purchase completed successfully for user: {} and product: {}", 
                           buyRequest.getUserId(), product.getName());
                return ResponseEntity.ok(successResponse);
            } else {
                // Payment failed
                BuyResponse errorResponse = new BuyResponse(false, "Payment failed", null, product.getName(), product.getPrice().doubleValue());
                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(errorResponse);
            }
            
        } catch (RestClientException e) {
            logger.error("Error calling payment service: {}", e.getMessage());
            BuyResponse errorResponse = new BuyResponse(false, "Payment service unavailable", null, null, null);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
        } catch (Exception e) {
            logger.error("Error processing purchase: {}", e.getMessage());
            BuyResponse errorResponse = new BuyResponse(false, "Purchase processing failed", null, null, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Catalog service with database is running!");
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        try {
            long totalProducts = productRepository.count();
            long inStockProducts = productRepository.countByStockQuantityGreaterThan(0);
            List<String> categories = productRepository.findAllCategories();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("service", "catalog-service");
            stats.put("status", "healthy");
            stats.put("totalProducts", totalProducts);
            stats.put("inStockProducts", inStockProducts);
            stats.put("totalCategories", categories.size());
            stats.put("timestamp", java.time.LocalDateTime.now().toString());
            stats.put("database", "PostgreSQL - catalog_schema");
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error fetching catalog stats: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
