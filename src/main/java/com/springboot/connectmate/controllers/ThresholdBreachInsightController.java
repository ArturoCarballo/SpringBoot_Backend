package com.springboot.connectmate.controllers;

import com.springboot.connectmate.dtos.Metric.ThresholdBreachInsightDTO;
import com.springboot.connectmate.enums.Status;
import com.springboot.connectmate.services.ThresholdBreachInsightService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/threshold-breach-insights")
@Tag(
        name = "Connectmate Threshold Breach Insights REST API",
        description = "CRUD REST API for Connectmate Threshold Breach Insights"
)
public class ThresholdBreachInsightController {

    private final ThresholdBreachInsightService service;

    @Autowired
    public ThresholdBreachInsightController(ThresholdBreachInsightService service) {
        this.service = service;
    }

    @GetMapping("/status/{statusString}")
    public ResponseEntity<List<ThresholdBreachInsightDTO>> getInsightsByStatus(@PathVariable String statusString) {
        Status status = Status.fromString(statusString);
        return ResponseEntity.ok(service.getInsightsByStatus(status));
    }

    @GetMapping("/item/{connectItemId}")
    public ResponseEntity<List<ThresholdBreachInsightDTO>> getInsightsByConnectItemId(@PathVariable String connectItemId) {
        return ResponseEntity.ok(service.getInsightsByConnectItemId(connectItemId));
    }
}
