package com.java.erp.modules.auth.entity;

import com.java.erp.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * User entity representing system users (department staff).
 * Fields: id, username, name, email, password, phone, staffCode, designation,
 *         deptRole, isActive, createdAt, updatedAt, createdBy, updatedBy.
 * Audit fields are inherited from BaseEntity.
 */
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    /**
     * Department role enum — maps to PostgreSQL 'dept_role' enum type.
     */
    public enum DeptRole {
        HOD, STAFF
    }

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "staff_code", unique = true, length = 20)
    private String staffCode;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "designation", length = 50)
    private String designation;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, columnDefinition = "dept_role")
    private DeptRole deptRole = DeptRole.STAFF;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public User() {
    }

    public User(String username, String name, String email, String password) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    /**
     * Convenience constructor for creating users without a username.
     * Username can be set separately if needed.
     */
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Helper methods

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    // Getters and Setters

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public DeptRole getDeptRole() {
        return deptRole;
    }

    public void setDeptRole(DeptRole deptRole) {
        this.deptRole = deptRole;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
