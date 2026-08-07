package com.java.erp.modules.schedule.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.java.erp.modules.schedule.entity.ScheduleParticipant;

public class ParticipantResponse {

    @JsonProperty("participant_id")
    private Long participantId;
    
    @JsonProperty("user_id")
    private Long userId;
    
    private String name;
    private String email;
    private String status;

    public ParticipantResponse() {
    }

    public static ParticipantResponse fromEntity(ScheduleParticipant participant) {
        ParticipantResponse response = new ParticipantResponse();
        response.setParticipantId(participant.getId());
        response.setUserId(participant.getUser().getId());
        response.setName(participant.getUser().getName());
        response.setEmail(participant.getUser().getEmail());
        response.setStatus(participant.getStatus().name());
        return response;
    }

    // Getters and Setters

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
