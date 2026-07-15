package com.erp.dto.response;

import java.util.Set;

/**
 * DTO for returning user information (without sensitive data like passwords).
 */
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private boolean active;
    private Set<String> roles;

    public UserResponse() {
    }

    public UserResponse(Long id, String name, String email,
                        String phone, boolean active, Set<String> roles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
