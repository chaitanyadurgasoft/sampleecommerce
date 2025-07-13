package com.example.catalogservice.repository;

import com.example.catalogservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Find products by category
    List<Product> findByCategory(String category);
    
    // Find products by name containing (search)
    List<Product> findByNameContainingIgnoreCase(String name);
    
    // Find products in price range
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    // Find products in stock
    List<Product> findByStockQuantityGreaterThan(Integer quantity);
    
    // Count products in stock
    long countByStockQuantityGreaterThan(Integer quantity);
    
    // Find products by category and in stock
    List<Product> findByCategoryAndStockQuantityGreaterThan(String category, Integer quantity);
    
    // Custom query to find featured products (top 8 by stock)
    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0 ORDER BY p.stockQuantity DESC")
    List<Product> findFeaturedProducts();
    
    // Get all categories
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL")
    List<String> findAllCategories();
    
    // Find low stock products
    List<Product> findByStockQuantityLessThan(Integer quantity);
    
    // Find products by price range and category
    List<Product> findByPriceBetweenAndCategory(BigDecimal minPrice, BigDecimal maxPrice, String category);
}
