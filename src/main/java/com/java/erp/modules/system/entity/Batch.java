package com.java.erp.modules.system.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Batch entity representing an academic year group.
 * Maps to the 'batches' table created in V3 migration.
 */
@Entity
@Table(name = "batches")
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "batch_name", nullable = false, unique = true, length = 20)
    private String batchName;

    @Column(name = "admission_year", nullable = false)
    private Integer admissionYear;

    @Column(name = "graduation_year", nullable = false)
    private Integer graduationYear;

    @Column(name = "current_year", nullable = false)
    private Short currentYear;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Constructors

    public Batch() {
    }

    public Batch(String batchName, Integer admissionYear, Integer graduationYear, Short currentYear) {
        this.batchName = batchName;
        this.admissionYear = admissionYear;
        this.graduationYear = graduationYear;
        this.currentYear = currentYear;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public Integer getAdmissionYear() {
        return admissionYear;
    }

    public void setAdmissionYear(Integer admissionYear) {
        this.admissionYear = admissionYear;
    }

    public Integer getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
    }

    public Short getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(Short currentYear) {
        this.currentYear = currentYear;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
