package com.java.erp.modules.schedule.entity;

import com.java.erp.modules.auth.entity.User;
import com.java.erp.modules.system.entity.Section;
import com.java.erp.modules.system.entity.Student;
import jakarta.persistence.*;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "student_schedules")
public class StudentSchedule {
    public enum Type { POLL, DEADLINE }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private Type type;
    @Column(nullable = false, length = 255)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String details;
    private LocalDate dueDate;
    private LocalTime dueTime;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @ManyToMany
    @JoinTable(name = "student_schedule_sections", joinColumns = @JoinColumn(name = "schedule_id"), inverseJoinColumns = @JoinColumn(name = "section_id"))
    private Set<Section> sections = new LinkedHashSet<>();
    @ManyToMany
    @JoinTable(name = "student_schedule_students", joinColumns = @JoinColumn(name = "schedule_id"), inverseJoinColumns = @JoinColumn(name = "student_id"))
    private Set<Student> students = new LinkedHashSet<>();
    @ElementCollection
    @CollectionTable(name = "poll_options", joinColumns = @JoinColumn(name = "schedule_id"))
    @Column(name = "option_text")
    @OrderColumn(name = "display_order")
    private List<String> options = new ArrayList<>();

    @PrePersist void onCreate() { createdAt = LocalDateTime.now(); }
    public Long getId(){return id;} public User getCreatedBy(){return createdBy;} public void setCreatedBy(User v){createdBy=v;}
    public Type getType(){return type;} public void setType(Type v){type=v;} public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getDetails(){return details;} public void setDetails(String v){details=v;} public LocalDate getDueDate(){return dueDate;} public void setDueDate(LocalDate v){dueDate=v;}
    public LocalTime getDueTime(){return dueTime;} public void setDueTime(LocalTime v){dueTime=v;} public LocalDateTime getCreatedAt(){return createdAt;}
    public Set<Section> getSections(){return sections;} public Set<Student> getStudents(){return students;} public List<String> getOptions(){return options;}
}
