package com.example.catalogservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller  // Note: @Controller, not @RestController
public class CatalogWebController {

    @GetMapping("/")
    public String catalog() {
        return "catalog";  // Returns src/main/resources/templates/catalog.html
    }
    
    @GetMapping("/catalog")
    public String catalogPage() {
        return "catalog";  // Returns src/main/resources/templates/catalog.html
    }
}
