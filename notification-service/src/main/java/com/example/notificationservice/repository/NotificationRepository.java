package com.example.notificationservice.repository;

import com.example.notificationservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Find notification by notification ID
    Optional<Notification> findByNotificationId(String notificationId);
    
    // Find all notifications for a user
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);
    
    // Find unread notifications for a user
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(String userId);
    
    // Find notifications by type
    List<Notification> findByType(String type);
    
    // Find notifications in date range
    List<Notification> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // Count unread notifications for a user
    long countByUserIdAndIsReadFalse(String userId);
    
    // Custom query - recent notifications (last 24 hours)
    @Query("SELECT n FROM Notification n WHERE n.createdAt > ?1 ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotifications(LocalDateTime since);
    
    // Get notification statistics
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.type = ?1")
    long countByType(String type);
    
    // Mark notifications as read for a user
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = ?1 AND n.isRead = false")
    void markAllAsReadForUser(String userId);
    
    // Find notifications by user and type
    List<Notification> findByUserIdAndType(String userId, String type);
    
    // Get top 10 recent notifications
    List<Notification> findTop10ByOrderByCreatedAtDesc();
}
