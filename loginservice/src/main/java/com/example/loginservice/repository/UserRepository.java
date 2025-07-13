package com.example.loginservice.repository;

import com.example.loginservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find user by username (for login)
    Optional<User> findByUsername(String username);
    
    // Check if username exists (for registration)
    boolean existsByUsername(String username);
    
    // Find users by email
    Optional<User> findByEmail(String email);
    
    // Find users created after a certain date
    List<User> findByCreatedAtAfter(LocalDateTime date);
    
    // Custom query to find users who logged in recently
    @Query("SELECT u FROM User u WHERE u.lastLogin > ?1")
    List<User> findRecentlyActiveUsers(LocalDateTime since);
    
    // Count total users
    long count();
}
