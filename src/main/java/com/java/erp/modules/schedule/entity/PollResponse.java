package com.java.erp.modules.schedule.entity;

import com.java.erp.modules.system.entity.Student;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "poll_responses", uniqueConstraints = @UniqueConstraint(columnNames = {"schedule_id", "student_id"}))
public class PollResponse {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "schedule_id", nullable = false)
    private StudentSchedule schedule;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    @Column(name = "selected_option_index", nullable = false)
    private Integer selectedOptionIndex;
    @Column(name = "responded_at", nullable = false, updatable = false)
    private LocalDateTime respondedAt;
    @PrePersist void onCreate() { respondedAt = LocalDateTime.now(); }

    public Long getId(){return id;} public StudentSchedule getSchedule(){return schedule;} public void setSchedule(StudentSchedule v){schedule=v;}
    public Student getStudent(){return student;} public void setStudent(Student v){student=v;}
    public Integer getSelectedOptionIndex(){return selectedOptionIndex;} public void setSelectedOptionIndex(Integer v){selectedOptionIndex=v;}
    public LocalDateTime getRespondedAt(){return respondedAt;}
}
