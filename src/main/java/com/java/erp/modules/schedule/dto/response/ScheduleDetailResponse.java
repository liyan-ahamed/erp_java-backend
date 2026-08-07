package com.java.erp.modules.schedule.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.java.erp.modules.schedule.entity.Schedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Detailed DTO for returning comprehensive schedule information,
 * including all enterprise fields and participant details.
 */
public class ScheduleDetailResponse extends ScheduleResponse {

    private String description;
    
    @JsonProperty("recurrence_rule")
    private String recurrenceRule;
    
    private String agenda;
    
    @JsonProperty("meeting_notes")
    private String meetingNotes;
    
    @JsonProperty("attachment_metadata")
    private String attachmentMetadata;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    private List<ParticipantResponse> participants;

    public ScheduleDetailResponse() {
        super();
    }

    public static ScheduleDetailResponse fromEntity(Schedule schedule, List<ParticipantResponse> participants) {
        ScheduleDetailResponse response = new ScheduleDetailResponse();
        
        // Base fields
        response.setId(schedule.getId());
        response.setScheduleType(schedule.getScheduleType().name());
        response.setTitle(schedule.getTitle());
        response.setScheduleDate(schedule.getScheduleDate());
        response.setScheduleTime(schedule.getScheduleTime());
        response.setEndTime(schedule.getEndTime());
        response.setLocation(schedule.getLocation());
        response.setStatus(schedule.getStatus().name());
        response.setPriority(schedule.getPriority().name());
        response.setStaffId(schedule.getStaff().getId());
        response.setStaffName(schedule.getStaff().getName());
        response.setCreatedBy(schedule.getCreatedByUser().getId());
        response.setCreatedByName(schedule.getCreatedByUser().getName());
        response.setCreatedAt(schedule.getCreatedAt());
        
        // Detail fields
        response.setDescription(schedule.getDescription());
        response.setRecurrenceRule(schedule.getRecurrenceRule());
        response.setAgenda(schedule.getAgenda());
        response.setMeetingNotes(schedule.getMeetingNotes());
        response.setAttachmentMetadata(schedule.getAttachmentMetadata());
        response.setUpdatedAt(schedule.getUpdatedAt());
        response.setParticipants(participants);
        
        return response;
    }

    // Getters and Setters

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getAttachmentMetadata() {
        return attachmentMetadata;
    }

    public void setAttachmentMetadata(String attachmentMetadata) {
        this.attachmentMetadata = attachmentMetadata;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ParticipantResponse> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ParticipantResponse> participants) {
        this.participants = participants;
    }
}
