package com.java.erp.modules.schedule.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO for updating an existing schedule.
 * Only provided fields are updated.
 */
public class UpdateScheduleRequest {

    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    private LocalDate date;

    private LocalTime time;

    @JsonProperty("end_time")
    private LocalTime endTime;

    private String description;

    private String location;

    private String status;

    private String priority;

    @JsonProperty("recurrence_rule")
    private String recurrenceRule;

    private String agenda;
    
    @JsonProperty("meeting_notes")
    private String meetingNotes;

    @JsonProperty("add_staff_ids")
    private List<Long> addStaffIds;
    
    @JsonProperty("remove_staff_ids")
    private List<Long> removeStaffIds;

    public UpdateScheduleRequest() {
    }

    // Getters and Setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getRecurrenceRule() {
        return recurrenceRule;
    }

    public void setRecurrenceRule(String recurrenceRule) {
        this.recurrenceRule = recurrenceRule;
    }

    public String getAgenda() {
        return agenda;
    }

    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }

    public String getMeetingNotes() {
        return meetingNotes;
    }

    public void setMeetingNotes(String meetingNotes) {
        this.meetingNotes = meetingNotes;
    }

    public List<Long> getAddStaffIds() {
        return addStaffIds;
    }

    public void setAddStaffIds(List<Long> addStaffIds) {
        this.addStaffIds = addStaffIds;
    }

    public List<Long> getRemoveStaffIds() {
        return removeStaffIds;
    }

    public void setRemoveStaffIds(List<Long> removeStaffIds) {
        this.removeStaffIds = removeStaffIds;
    }
}
