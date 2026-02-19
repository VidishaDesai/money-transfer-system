package com.example.money_transfer_system.controller;

import com.example.money_transfer_system.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/kpis")
    public ResponseEntity<Map<String, Object>> getKpis() {
        return ResponseEntity.ok(analyticsService.getKpis());
    }
}
