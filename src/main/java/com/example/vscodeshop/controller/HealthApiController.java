package com.example.vscodeshop.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthApiController {
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "ok", true,
                "message", "Spring Boot API 正常",
                "architecture", "React + Spring Boot + MyBatis XML + MySQL"
        );
    }
}
