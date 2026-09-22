package com.java.erp.modules.schedule.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;

public class CreateStudentScheduleRequest {
    @NotBlank private String type;
    @NotBlank @Size(max=255) private String title;
    private String details;
    @JsonProperty("due_date") private LocalDate dueDate;
    @JsonProperty("due_time") private LocalTime dueTime;
    @JsonProperty("section_ids") private Set<Long> sectionIds = new HashSet<>();
    @JsonProperty("student_ids") private Set<Long> studentIds = new HashSet<>();
    private List<String> options = new ArrayList<>();
    public String getType(){return type;} public void setType(String v){type=v;} public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getDetails(){return details;} public void setDetails(String v){details=v;} public LocalDate getDueDate(){return dueDate;} public void setDueDate(LocalDate v){dueDate=v;}
    public LocalTime getDueTime(){return dueTime;} public void setDueTime(LocalTime v){dueTime=v;} public Set<Long> getSectionIds(){return sectionIds;} public void setSectionIds(Set<Long> v){sectionIds=v;}
    public Set<Long> getStudentIds(){return studentIds;} public void setStudentIds(Set<Long> v){studentIds=v;} public List<String> getOptions(){return options;} public void setOptions(List<String> v){options=v;}
}
