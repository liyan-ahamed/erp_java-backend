package com.java.erp.modules.auth.dto.response;

import java.util.Set;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for returning user information (without sensitive data like passwords).
 */
public class UserResponse {

    private Long id;
    private String username;
    private String name;
    private String email;
    @JsonProperty("staff_code")
    private String staffCode;
    private String phone;
    private String designation;
    @JsonProperty("is_active")
    private boolean active;
    @Schema(example = "[\"ROLE_STAFF\"]")
    private Set<String> roles;

    public UserResponse() {
    }

    public UserResponse(Long id, String username, String name, String email,
                        String staffCode, String phone, String designation, 
                        boolean active, Set<String> roles) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.staffCode = staffCode;
        this.phone = phone;
        this.designation = designation;
        this.active = active;
        this.roles = roles;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
