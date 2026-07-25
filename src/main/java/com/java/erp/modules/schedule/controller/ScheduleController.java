package com.java.erp.modules.schedule.controller;

import com.java.erp.common.response.ApiResponse;
import com.java.erp.common.util.SecurityUtils;
import com.java.erp.modules.auth.dto.response.UserResponse;
import com.java.erp.modules.auth.entity.Role;
import com.java.erp.modules.auth.entity.User;
import com.java.erp.modules.schedule.dto.request.CreateScheduleRequest;
import com.java.erp.modules.schedule.dto.response.ScheduleResponse;
import com.java.erp.modules.schedule.entity.ScheduleType;
import com.java.erp.modules.schedule.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controller for schedule management endpoints.
 * All schedule-related functionality is exposed under /schedules.
 */
@RestController
@RequestMapping("/schedules")
@Tag(name = "Schedules", description = "Endpoints for schedule management")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/staff")
    @PreAuthorize("hasRole('HOD')")
    @Operation(summary = "Get all staff members",
               description = "Returns all active staff members for schedule assignment")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Staff list fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Not authenticated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllStaff() {
        List<User> staffMembers = scheduleService.getAllStaff();
        List<UserResponse> response = staffMembers.stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Staff list fetched successfully", response));
    }

    @PostMapping
    @PreAuthorize("hasRole('HOD')")
    @Operation(summary = "Create schedule(s)",
               description = "Create schedule(s) for one or more staff members")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "Schedule(s) created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Invalid request data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Not authenticated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> createSchedules(
            @Valid @RequestBody CreateScheduleRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<ScheduleResponse> schedules = scheduleService.createSchedules(request, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Schedule(s) created successfully", schedules));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HOD', 'STAFF')")
    @Operation(summary = "Get schedules",
               description = "HOD: returns schedules they created. Staff: returns schedules assigned to them. " +
                             "Optionally filter by schedule type (DEADLINE or MEETING).")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Schedules fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getSchedules(
            @RequestParam(value = "type", required = false) ScheduleType type) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean isHod = SecurityUtils.hasAuthority("ROLE_HOD");
        List<ScheduleResponse> schedules = scheduleService.getSchedules(type, currentUserId, isHod);
        return ResponseEntity.ok(ApiResponse.success("Schedules fetched successfully", schedules));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HOD')")
    @Operation(summary = "Delete a schedule",
               description = "Delete a schedule. Only the creator (HOD) can delete their own schedules.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Schedule deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Not authenticated or not the creator"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Schedule not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(@PathVariable Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        scheduleService.deleteSchedule(id, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Schedule deleted successfully"));
    }

    /**
     * Map a User entity to UserResponse DTO.
     * Reuses the existing UserResponse from the auth module.
     */
    private UserResponse toUserResponse(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getStaffCode(),
                user.getPhone(),
                user.getDesignation(),
                user.isActive(),
                roleNames
        );
    }
}
