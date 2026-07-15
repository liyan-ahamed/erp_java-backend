package com.erp.controller;

import com.erp.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Health check controller providing application status information.
 * This endpoint is publicly accessible.
 */
@RestController
@RequestMapping("/health")
@Tag(name = "Health", description = "Application health check endpoints")
public class HealthController {

    @GetMapping
    @Operation(summary = "Health check", description = "Returns application health status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        Map<String, Object> healthData = Map.of(
                "status", "UP",
                "application", "ERP Backend",
                "timestamp", LocalDateTime.now().toString()
        );
        return ResponseEntity.ok(ApiResponse.success("Application is running", healthData));
    }
}
