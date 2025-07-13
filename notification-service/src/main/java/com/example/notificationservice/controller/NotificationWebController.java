package com.example.notificationservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NotificationWebController {
    
    @GetMapping("/")
    public String notifications() {
        return "notifications";  // Returns templates/notifications.html
    }
    
    @GetMapping("/notifications")
    public String notificationsPage() {
        return "notifications";
    }
}
