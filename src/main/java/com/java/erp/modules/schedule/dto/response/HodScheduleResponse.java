package com.java.erp.modules.schedule.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * HOD-specific schedule response DTO.
 * Extends ScheduleResponse to include the completion status field.
 */
public class HodScheduleResponse extends ScheduleResponse {

    private boolean completed;

    public HodScheduleResponse() {
    }

    public HodScheduleResponse(Long id, String scheduleType, String title,
                                LocalDate scheduleDate, LocalTime scheduleTime,
                                Long staffId, String staffName,
                                Long createdBy, String createdByName,
                                LocalDateTime createdAt, boolean completed) {
        super(id, scheduleType, title, scheduleDate, scheduleTime,
              staffId, staffName, createdBy, createdByName, createdAt);
        this.completed = completed;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
