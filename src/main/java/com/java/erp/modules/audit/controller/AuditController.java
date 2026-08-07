package com.java.erp.modules.audit.controller;

import com.java.erp.common.response.ApiResponse;
import com.java.erp.common.response.PagedResponse;
import com.java.erp.modules.audit.dto.response.AuditLogResponse;
import com.java.erp.modules.audit.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/audit-logs")
@Tag(name = "Audit Logs", description = "Endpoints for viewing system audit trails")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_AUDIT_LOGS')")
    @Operation(summary = "Get audit logs", description = "Returns a paginated list of system audit logs. Requires VIEW_AUDIT_LOGS permission.")
    public ResponseEntity<ApiResponse<PagedResponse<AuditLogResponse>>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PagedResponse<AuditLogResponse> logs = auditService.getAuditLogs(page, size);
        return ResponseEntity.ok(ApiResponse.success("Audit logs fetched successfully", logs));
    }
}
