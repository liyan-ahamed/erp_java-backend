package com.java.erp.modules.schedule.service;

import com.java.erp.exception.*;
import com.java.erp.modules.auth.entity.User;
import com.java.erp.modules.auth.repository.UserRepository;
import com.java.erp.modules.schedule.dto.request.CreateStudentScheduleRequest;
import com.java.erp.modules.schedule.dto.request.SubmitPollResponseRequest;
import com.java.erp.modules.schedule.dto.response.*;
import com.java.erp.modules.schedule.entity.StudentSchedule;
import com.java.erp.modules.schedule.entity.PollResponse;
import com.java.erp.modules.schedule.repository.StudentScheduleRepository;
import com.java.erp.modules.schedule.repository.PollResponseRepository;
import com.java.erp.modules.system.entity.*;
import com.java.erp.modules.system.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class StudentScheduleService {
    private final StudentScheduleRepository repository;
    private final UserRepository users;
    private final StudentRepository students;
    private final SectionRepository sections;
    private final PollResponseRepository pollResponses;

    public StudentScheduleService(StudentScheduleRepository repository, UserRepository users,
                                  StudentRepository students, SectionRepository sections,
                                  PollResponseRepository pollResponses) {
        this.repository = repository; this.users = users; this.students = students; this.sections = sections;
        this.pollResponses = pollResponses;
    }

    @Transactional(readOnly = true)
    public ScheduleAudienceResponse audience() {
        var classItems = sections.findAllByOrderByBatchCurrentYearAscSectionNameAsc().stream()
            .map(s -> new ScheduleAudienceResponse.ClassItem(s.getId(), s.getBatch().getBatchName() + " - " + s.getSectionName())).toList();
        var studentItems = students.findByActiveTrueOrderByNameAsc().stream()
            .map(s -> new ScheduleAudienceResponse.StudentItem(s.getId(), s.getName(), s.getRegisterNumber(), s.getSection().getId())).toList();
        return new ScheduleAudienceResponse(classItems, studentItems);
    }

    @Transactional
    public StudentScheduleResponse create(CreateStudentScheduleRequest request, Long creatorId) {
        User creator = users.findById(creatorId).orElseThrow(() -> new UnauthorizedException("Authenticated user not found"));
        StudentSchedule.Type type;
        try { type = StudentSchedule.Type.valueOf(request.getType().toUpperCase()); }
        catch (IllegalArgumentException ex) { throw new BadRequestException("Type must be POLL or DEADLINE"); }
        Set<Long> sectionIds = request.getSectionIds() == null ? Set.of() : request.getSectionIds();
        Set<Long> studentIds = request.getStudentIds() == null ? Set.of() : request.getStudentIds();
        if (sectionIds.isEmpty() && studentIds.isEmpty()) throw new BadRequestException("Select at least one class or student");
        if (type == StudentSchedule.Type.POLL && (request.getOptions() == null || request.getOptions().size() < 2))
            throw new BadRequestException("A poll requires at least two options");
        if (type == StudentSchedule.Type.DEADLINE && request.getDueDate() == null)
            throw new BadRequestException("A deadline date is required");

        StudentSchedule schedule = new StudentSchedule();
        schedule.setCreatedBy(creator); schedule.setType(type); schedule.setTitle(request.getTitle().trim());
        schedule.setDetails(request.getDetails()); schedule.setDueDate(request.getDueDate()); schedule.setDueTime(request.getDueTime());
        if (!sectionIds.isEmpty()) schedule.getSections().addAll(requireSections(sectionIds));
        if (!studentIds.isEmpty()) schedule.getStudents().addAll(requireStudents(studentIds));
        if (type == StudentSchedule.Type.POLL) request.getOptions().stream().map(String::trim).filter(v -> !v.isEmpty()).forEach(schedule.getOptions()::add);
        if (type == StudentSchedule.Type.POLL && schedule.getOptions().size() < 2) throw new BadRequestException("A poll requires at least two non-empty options");
        return toResponse(repository.save(schedule), null);
    }

    @Transactional(readOnly = true)
    public List<StudentScheduleResponse> list(StudentSchedule.Type type, Long userId, boolean hod, boolean studentRole) {
        List<StudentSchedule> result;
        Long responseStudentId = null;
        if (hod) result = repository.findByTypeOrderByCreatedAtDesc(type);
        else if (studentRole) {
            User user = users.findById(userId).orElseThrow(() -> new UnauthorizedException("Authenticated user not found"));
            Student student = students.findByUserId(userId).or(() -> students.findByEmailIgnoreCase(user.getEmail()))
                .orElseThrow(() -> new BadRequestException("This Student account is not linked to a student record"));
            result = repository.findAssigned(type, student.getSection().getId(), student.getId());
            responseStudentId = student.getId();
        } else result = repository.findByTypeAndCreatedByIdOrderByCreatedAtDesc(type, userId);
        Long studentId = responseStudentId;
        return result.stream().map(schedule -> toResponse(schedule, studentId)).toList();
    }

    @Transactional
    public StudentScheduleResponse respond(Long scheduleId, SubmitPollResponseRequest request, Long userId) {
        User user = users.findById(userId).orElseThrow(() -> new UnauthorizedException("Authenticated user not found"));
        Student student = students.findByUserId(userId).or(() -> students.findByEmailIgnoreCase(user.getEmail()))
            .orElseThrow(() -> new BadRequestException("This Student account is not linked to a student record"));
        StudentSchedule poll = repository.findById(scheduleId)
            .orElseThrow(() -> new ResourceNotFoundException("Poll", "id", scheduleId));
        if (poll.getType() != StudentSchedule.Type.POLL) throw new BadRequestException("Responses can only be submitted for polls");
        boolean assigned = poll.getStudents().stream().anyMatch(value -> value.getId().equals(student.getId()))
            || poll.getSections().stream().anyMatch(value -> value.getId().equals(student.getSection().getId()));
        if (!assigned) throw new UnauthorizedException("This poll is not assigned to you");
        if (pollResponses.existsByScheduleIdAndStudentId(scheduleId, student.getId()))
            throw new BadRequestException("You have already responded to this poll");
        int optionIndex = request.getOptionIndex();
        if (optionIndex < 0 || optionIndex >= poll.getOptions().size()) throw new BadRequestException("Invalid poll option");
        PollResponse response = new PollResponse();
        response.setSchedule(poll); response.setStudent(student); response.setSelectedOptionIndex(optionIndex);
        pollResponses.save(response);
        return toResponse(poll, student.getId());
    }

    private List<Section> requireSections(Set<Long> ids) {
        List<Section> found = sections.findAllById(ids);
        if (found.size() != ids.size()) throw new BadRequestException("One or more selected classes do not exist");
        return found;
    }
    private List<Student> requireStudents(Set<Long> ids) {
        List<Student> found = students.findAllById(ids);
        if (found.size() != ids.size()) throw new BadRequestException("One or more selected students do not exist");
        return found;
    }
    private StudentScheduleResponse toResponse(StudentSchedule s, Long studentId) {
        PollResponse response = studentId == null || s.getType() != StudentSchedule.Type.POLL ? null
            : pollResponses.findByScheduleIdAndStudentId(s.getId(), studentId).orElse(null);
        return new StudentScheduleResponse(s.getId(), s.getType().name(), s.getTitle(), s.getDetails(), s.getDueDate(), s.getDueTime(),
            List.copyOf(s.getOptions()), s.getSections().stream().map(v -> v.getBatch().getBatchName()+" - "+v.getSectionName()).toList(),
            s.getStudents().stream().map(Student::getName).toList(), s.getCreatedBy().getName(), s.getCreatedAt(),
            response != null, response == null ? null : response.getSelectedOptionIndex());
    }
}
