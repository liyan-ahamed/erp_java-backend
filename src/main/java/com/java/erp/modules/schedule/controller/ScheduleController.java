package com.java.erp.modules.schedule.controller;

import com.java.erp.common.mapper.UserMapper;
import com.java.erp.common.response.ApiResponse;
import com.java.erp.common.util.SecurityUtils;
import com.java.erp.modules.auth.dto.response.UserResponse;
import com.java.erp.modules.auth.entity.User;
import com.java.erp.modules.schedule.dto.request.CreateScheduleRequest;
import com.java.erp.modules.schedule.dto.request.UpdateScheduleRequest;
import com.java.erp.modules.schedule.dto.response.ScheduleDetailResponse;
import com.java.erp.modules.schedule.dto.response.ScheduleResponse;
import com.java.erp.modules.schedule.entity.ScheduleType;
import com.java.erp.modules.schedule.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for enterprise schedule management endpoints.
 */
@RestController
@RequestMapping("/schedules")
@Tag(name = "Schedules", description = "Endpoints for enterprise schedule management")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    // Legacy HOD -> Staff target lookup. Restore these mapping/security annotations
    // together with the legacy create endpoint below if that workflow returns.
    // @GetMapping("/staff")
    // @PreAuthorize("hasAuthority('CREATE_SCHEDULE') or hasAuthority('EDIT_SCHEDULE')")
    @Operation(summary = "Get all staff members",
               description = "Returns all active staff members for schedule assignment")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllStaff() {
        List<User> staffMembers = scheduleService.getAllStaff();
        List<UserResponse> response = staffMembers.stream()
                .map(UserMapper::toUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Staff list fetched successfully", response));
    }

    // Legacy HOD -> Staff schedule creation retained for future restoration.
    // @PostMapping
    // @PreAuthorize("hasAuthority('CREATE_SCHEDULE')")
    @Operation(summary = "Create schedule(s)",
               description = "Create schedule(s) for one or more staff members")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> createSchedules(
            @Valid @RequestBody CreateScheduleRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<ScheduleResponse> schedules = scheduleService.createSchedules(request, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Schedule(s) created successfully", schedules));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_SCHEDULE')")
    @Operation(summary = "Get schedules",
               description = "Returns schedules. HOD sees all they created, Staff sees assigned.")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getSchedules(
            @RequestParam(value = "type", required = false) ScheduleType type) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean isHod = SecurityUtils.hasAuthority("ROLE_HOD");
        List<ScheduleResponse> schedules = scheduleService.getSchedules(type, currentUserId, isHod);
        return ResponseEntity.ok(ApiResponse.success("Schedules fetched successfully", schedules));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_SCHEDULE')")
    @Operation(summary = "Get schedule detail",
               description = "Get comprehensive details of a schedule including participants")
    public ResponseEntity<ApiResponse<ScheduleDetailResponse>> getScheduleDetail(@PathVariable Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        ScheduleDetailResponse schedule = scheduleService.getScheduleDetail(id, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Schedule detail fetched successfully", schedule));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDIT_SCHEDULE')")
    @Operation(summary = "Update schedule",
               description = "Update an existing schedule")
    public ResponseEntity<ApiResponse<ScheduleDetailResponse>> updateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody UpdateScheduleRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        ScheduleDetailResponse schedule = scheduleService.updateSchedule(id, request, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Schedule updated successfully", schedule));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_SCHEDULE')")
    @Operation(summary = "Delete a schedule",
               description = "Delete a schedule.")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(@PathVariable Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        scheduleService.deleteSchedule(id, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Schedule deleted successfully"));
    }
}
