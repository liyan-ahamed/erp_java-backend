package com.java.erp.modules.schedule.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO for returning schedule information.
 */
public class ScheduleResponse {

    private Long id;

    @JsonProperty("schedule_type")
    private String scheduleType;

    private String title;

    @JsonProperty("schedule_date")
    private LocalDate scheduleDate;

    @JsonProperty("schedule_time")
    private LocalTime scheduleTime;

    @JsonProperty("staff_id")
    private Long staffId;

    @JsonProperty("staff_name")
    private String staffName;

    @JsonProperty("created_by")
    private Long createdBy;

    @JsonProperty("created_by_name")
    private String createdByName;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    public ScheduleResponse() {
    }

    public ScheduleResponse(Long id, String scheduleType, String title,
                             LocalDate scheduleDate, LocalTime scheduleTime,
                             Long staffId, String staffName,
                             Long createdBy, String createdByName,
                             LocalDateTime createdAt) {
        this.id = id;
        this.scheduleType = scheduleType;
        this.title = title;
        this.scheduleDate = scheduleDate;
        this.scheduleTime = scheduleTime;
        this.staffId = staffId;
        this.staffName = staffName;
        this.createdBy = createdBy;
        this.createdByName = createdByName;
        this.createdAt = createdAt;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public LocalTime getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(LocalTime scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
