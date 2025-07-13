package com.example.paymentservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaymentWebController {

    @GetMapping("/")
    public String payment() {
        return "payment";  // Returns src/main/resources/templates/payment.html
    }
    
    @GetMapping("/payment")
    public String paymentPage() {
        return "payment";  // Returns src/main/resources/templates/payment.html
    }
}
