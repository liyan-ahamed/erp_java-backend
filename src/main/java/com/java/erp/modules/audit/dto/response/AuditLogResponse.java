package com.java.erp.modules.audit.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.java.erp.modules.audit.entity.AuditLog;

import java.time.LocalDateTime;

public class AuditLogResponse {

    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    private String username;
    private String action;
    private String module;

    @JsonProperty("entity_type")
    private String entityType;

    @JsonProperty("entity_id")
    private Long entityId;

    @JsonProperty("old_value")
    private String oldValue;

    @JsonProperty("new_value")
    private String newValue;

    @JsonProperty("ip_address")
    private String ipAddress;

    @JsonProperty("user_agent")
    private String userAgent;

    private LocalDateTime timestamp;

    public AuditLogResponse() {
    }

    public static AuditLogResponse fromEntity(AuditLog log) {
        AuditLogResponse response = new AuditLogResponse();
        response.setId(log.getId());
        response.setUserId(log.getUser() != null ? log.getUser().getId() : null);
        response.setUsername(log.getUsername());
        response.setAction(log.getAction());
        response.setModule(log.getModule());
        response.setEntityType(log.getEntityType());
        response.setEntityId(log.getEntityId());
        response.setOldValue(log.getOldValue());
        response.setNewValue(log.getNewValue());
        response.setIpAddress(log.getIpAddress());
        response.setUserAgent(log.getUserAgent());
        response.setTimestamp(log.getTimestamp());
        return response;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
