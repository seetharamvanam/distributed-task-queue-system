package com.seetha.taskqueue.metrics.controller;

import com.seetha.taskqueue.metrics.dto.MetricsResponseDTO;
import com.seetha.taskqueue.metrics.service.MetricsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    private final MetricsService metricsService;

    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping
    public ResponseEntity<MetricsResponseDTO> getMetrics() {
        return ResponseEntity.ok(metricsService.getTaskMetrics());
    }
}
