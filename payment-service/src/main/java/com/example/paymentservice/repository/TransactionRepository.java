package com.example.paymentservice.repository;

import com.example.paymentservice.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    // Find transaction by transaction ID
    Optional<Transaction> findByTransactionId(String transactionId);
    
    // Find all transactions for a user
    List<Transaction> findByUserIdOrderByCreatedAtDesc(String userId);
    
    // Find transactions by status
    List<Transaction> findByStatus(Transaction.TransactionStatus status);
    
    // Find successful transactions for a user
    List<Transaction> findByUserIdAndStatus(String userId, Transaction.TransactionStatus status);
    
    // Find transactions in date range
    List<Transaction> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // Custom query - total revenue from successful transactions
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.status = 'SUCCESS'")
    BigDecimal getTotalRevenue();
    
    // Custom query - transaction count by status
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.status = ?1")
    long countByStatus(Transaction.TransactionStatus status);
    
    // Recent transactions for dashboard
    List<Transaction> findTop10ByOrderByCreatedAtDesc();
    
    // Find transactions by product
    List<Transaction> findByProductId(Long productId);
    
    // Find transactions by payment method
    List<Transaction> findByPaymentMethod(String paymentMethod);
}
