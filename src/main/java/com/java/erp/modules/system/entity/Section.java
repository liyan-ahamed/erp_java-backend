package com.java.erp.modules.system.entity;

import com.java.erp.modules.auth.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Section entity representing a class section within a batch.
 * Maps to the 'sections' table created in V4 migration.
 * Has FK relationships to Batch and User (class advisor).
 */
@Entity
@Table(name = "sections", uniqueConstraints = {
        @UniqueConstraint(name = "uq_sections_batch_section", columnNames = {"batch_id", "section_name"})
})
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

    @Column(name = "section_name", nullable = false, length = 5)
    private String sectionName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_advisor_id")
    private User classAdvisor;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Constructors

    public Section() {
    }

    public Section(Batch batch, String sectionName) {
        this.batch = batch;
        this.sectionName = sectionName;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Batch getBatch() {
        return batch;
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public User getClassAdvisor() {
        return classAdvisor;
    }

    public void setClassAdvisor(User classAdvisor) {
        this.classAdvisor = classAdvisor;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
