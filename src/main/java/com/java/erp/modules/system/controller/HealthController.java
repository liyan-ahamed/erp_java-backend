package com.java.erp.modules.system.controller;

import com.java.erp.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for system health checks.
 */
@RestController
@RequestMapping("/health")
@Tag(name = "System Health", description = "Endpoints for checking system status")
public class HealthController {

    @GetMapping
    @Operation(summary = "Check system health", description = "Returns the current status of the API")
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now());
        data.put("version", "1.0.0");

        return ResponseEntity.ok(ApiResponse.success("API is running", data));
    }
}
