package com.example.loginservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller  // Note: @Controller, not @RestController
public class WebController {

    @GetMapping("/")
    public String index() {
        return "index";  // Returns src/main/resources/templates/index.html
    }
    
    @GetMapping("/login")
    public String login() {
        return "index";  // Returns src/main/resources/templates/index.html
    }
}
