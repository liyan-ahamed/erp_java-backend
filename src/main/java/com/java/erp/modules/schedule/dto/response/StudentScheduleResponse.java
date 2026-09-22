package com.java.erp.modules.schedule.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.*;
import java.util.List;

public record StudentScheduleResponse(
    Long id,
    String type,
    String title,
    String details,
    @JsonProperty("due_date") LocalDate dueDate,
    @JsonProperty("due_time") LocalTime dueTime,
    List<String> options,
    List<String> classes,
    List<String> students,
    @JsonProperty("created_by_name") String createdByName,
    @JsonProperty("created_at") LocalDateTime createdAt,
    boolean responded,
    @JsonProperty("selected_option_index") Integer selectedOptionIndex
) {}
