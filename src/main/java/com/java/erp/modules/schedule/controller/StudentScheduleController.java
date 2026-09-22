package com.java.erp.modules.schedule.controller;

import com.java.erp.common.response.ApiResponse;
import com.java.erp.common.util.SecurityUtils;
import com.java.erp.modules.schedule.dto.request.CreateStudentScheduleRequest;
import com.java.erp.modules.schedule.dto.request.SubmitPollResponseRequest;
import com.java.erp.modules.schedule.dto.response.*;
import com.java.erp.modules.schedule.entity.StudentSchedule;
import com.java.erp.modules.schedule.service.StudentScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/student-schedules")
public class StudentScheduleController {
    private final StudentScheduleService service;
    public StudentScheduleController(StudentScheduleService service) { this.service = service; }

    @GetMapping("/audience")
    @PreAuthorize("hasRole('STAFF')")
    public ApiResponse<ScheduleAudienceResponse> audience() {
        return ApiResponse.success("Schedule audience fetched successfully", service.audience());
    }

    @PostMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<ApiResponse<StudentScheduleResponse>> create(@Valid @RequestBody CreateStudentScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Schedule created successfully",
            service.create(request, SecurityUtils.getCurrentUserId())));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HOD','STAFF','STUDENT')")
    public ApiResponse<List<StudentScheduleResponse>> list(@RequestParam StudentSchedule.Type type) {
        return ApiResponse.success("Schedules fetched successfully", service.list(type, SecurityUtils.getCurrentUserId(),
            SecurityUtils.hasAuthority("ROLE_HOD"), SecurityUtils.hasAuthority("ROLE_STUDENT")));
    }

    @PostMapping("/{id}/response")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<StudentScheduleResponse> respond(@PathVariable Long id,
            @Valid @RequestBody SubmitPollResponseRequest request) {
        return ApiResponse.success("Poll response recorded", service.respond(id, request, SecurityUtils.getCurrentUserId()));
    }
}
