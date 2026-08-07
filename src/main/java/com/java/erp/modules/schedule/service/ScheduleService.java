package com.java.erp.modules.schedule.service;

import com.java.erp.common.audit.Auditable;
import com.java.erp.exception.BadRequestException;
import com.java.erp.exception.ResourceNotFoundException;
import com.java.erp.exception.UnauthorizedException;
import com.java.erp.modules.auth.entity.User;
import com.java.erp.modules.auth.repository.UserRepository;
import com.java.erp.modules.notification.entity.Notification;
import com.java.erp.modules.notification.service.NotificationService;
import com.java.erp.modules.schedule.dto.request.CreateScheduleRequest;
import com.java.erp.modules.schedule.dto.request.UpdateScheduleRequest;
import com.java.erp.modules.schedule.dto.response.ParticipantResponse;
import com.java.erp.modules.schedule.dto.response.ScheduleDetailResponse;
import com.java.erp.modules.schedule.dto.response.ScheduleResponse;
import com.java.erp.modules.schedule.entity.Schedule;
import com.java.erp.modules.schedule.entity.ScheduleParticipant;
import com.java.erp.modules.schedule.entity.SchedulePriority;
import com.java.erp.modules.schedule.entity.ScheduleStatus;
import com.java.erp.modules.schedule.entity.ScheduleType;
import com.java.erp.modules.schedule.repository.ScheduleParticipantRepository;
import com.java.erp.modules.schedule.repository.ScheduleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for enterprise schedule management operations.
 */
@Service
public class ScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleService.class);

    private static final int COMPLETED_SCHEDULES_LIMIT = 25;

    private final ScheduleRepository scheduleRepository;
    private final ScheduleParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ScheduleService(ScheduleRepository scheduleRepository,
                           ScheduleParticipantRepository participantRepository,
                           UserRepository userRepository,
                           NotificationService notificationService) {
        this.scheduleRepository = scheduleRepository;
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    /**
     * Create a schedule. Supports single or multi-user assignment.
     */
    @Transactional
    @Auditable(action = "CREATE", module = "SCHEDULING", entityType = "Schedule")
    public List<ScheduleResponse> createSchedules(CreateScheduleRequest request, Long currentUserId) {
        User creator = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UnauthorizedException("Authenticated user not found"));

        ScheduleType scheduleType;
        try {
            scheduleType = ScheduleType.valueOf(request.getScheduleType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid schedule type");
        }

        SchedulePriority priority = SchedulePriority.NORMAL;
        if (StringUtils.hasText(request.getPriority())) {
            try {
                priority = SchedulePriority.valueOf(request.getPriority().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid priority");
            }
        }

        List<Schedule> createdSchedules = new ArrayList<>();
        
        for (Long staffId : request.getStaffIds()) {
            User staff = userRepository.findById(staffId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", staffId));
            
            Schedule schedule = new Schedule();
            schedule.setStaff(staff);
            schedule.setCreatedByUser(creator);
            schedule.setScheduleType(scheduleType);
            schedule.setTitle(request.getTitle());
            schedule.setScheduleDate(request.getDate());
            schedule.setScheduleTime(request.getTime());
            schedule.setEndTime(request.getEndTime());
            schedule.setDescription(request.getDescription());
            schedule.setLocation(request.getLocation());
            schedule.setPriority(priority);
            schedule.setRecurrenceRule(request.getRecurrenceRule());
            schedule.setAgenda(request.getAgenda());
            
            Schedule saved = scheduleRepository.save(schedule);
            createdSchedules.add(saved);

            // Create participant record
            ScheduleParticipant participant = new ScheduleParticipant(saved, staff);
            participantRepository.save(participant);

            // Send notification
            notificationService.send(
                staffId,
                Notification.NotificationType.SCHEDULE,
                "New Schedule: " + saved.getTitle(),
                "You have a new " + scheduleType.name().toLowerCase() + " scheduled on " + saved.getScheduleDate(),
                "SCHEDULING",
                saved.getId(),
                "Schedule",
                Notification.NotificationPriority.valueOf(priority.name())
            );
        }

        return createdSchedules.stream().map(this::toResponse).collect(Collectors.toList());
    }

    /**
     * Update an existing schedule.
     */
    @Transactional
    @Auditable(action = "UPDATE", module = "SCHEDULING", entityType = "Schedule")
    public ScheduleDetailResponse updateSchedule(Long scheduleId, UpdateScheduleRequest request, Long currentUserId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", scheduleId));

        if (StringUtils.hasText(request.getTitle())) {
            schedule.setTitle(request.getTitle());
        }
        if (request.getDate() != null) {
            schedule.setScheduleDate(request.getDate());
        }
        if (request.getTime() != null) {
            schedule.setScheduleTime(request.getTime());
        }
        if (request.getEndTime() != null) {
            schedule.setEndTime(request.getEndTime());
        }
        if (request.getDescription() != null) {
            schedule.setDescription(request.getDescription());
        }
        if (request.getLocation() != null) {
            schedule.setLocation(request.getLocation());
        }
        if (StringUtils.hasText(request.getStatus())) {
            schedule.setStatus(ScheduleStatus.valueOf(request.getStatus().toUpperCase()));
        }
        if (StringUtils.hasText(request.getPriority())) {
            schedule.setPriority(SchedulePriority.valueOf(request.getPriority().toUpperCase()));
        }
        if (request.getRecurrenceRule() != null) {
            schedule.setRecurrenceRule(request.getRecurrenceRule());
        }
        if (request.getAgenda() != null) {
            schedule.setAgenda(request.getAgenda());
        }
        if (request.getMeetingNotes() != null) {
            schedule.setMeetingNotes(request.getMeetingNotes());
        }

        Schedule saved = scheduleRepository.save(schedule);
        
        // Notify participant about the update
        if (!schedule.getStaff().getId().equals(currentUserId)) {
            notificationService.send(
                schedule.getStaff().getId(),
                Notification.NotificationType.SCHEDULE,
                "Schedule Updated: " + schedule.getTitle(),
                "A schedule assigned to you has been updated.",
                "SCHEDULING",
                schedule.getId(),
                "Schedule",
                Notification.NotificationPriority.NORMAL
            );
        }

        return getScheduleDetail(saved.getId(), currentUserId);
    }

    /**
     * Get detailed information for a single schedule.
     */
    @Transactional(readOnly = true)
    public ScheduleDetailResponse getScheduleDetail(Long scheduleId, Long currentUserId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", scheduleId));

        List<ScheduleParticipant> participants = participantRepository.findByScheduleId(scheduleId);
        List<ParticipantResponse> participantResponses = participants.stream()
                .map(ParticipantResponse::fromEntity)
                .collect(Collectors.toList());

        return ScheduleDetailResponse.fromEntity(schedule, participantResponses);
    }

    /**
     * Get schedules based on user role (now unified).
     */
    @Transactional
    public List<ScheduleResponse> getSchedules(ScheduleType type, Long currentUserId, boolean isHod) {
        if (isHod) {
            markCompletedSchedules(currentUserId);

            List<Schedule> activeSchedules = scheduleRepository
                    .findActiveByCreatorAndOptionalType(currentUserId, type);

            List<Schedule> completedSchedules = scheduleRepository
                    .findCompletedByCreatorAndOptionalType(currentUserId, type,
                            PageRequest.of(0, COMPLETED_SCHEDULES_LIMIT));

            List<ScheduleResponse> response = new ArrayList<>();
            activeSchedules.stream().map(this::toResponse).forEach(response::add);
            completedSchedules.stream().map(this::toResponse).forEach(response::add);
            return response;
        } else {
            List<Schedule> schedules = scheduleRepository
                    .findByStaffAndOptionalType(currentUserId, type);
            return schedules.stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    @Auditable(action = "DELETE", module = "SCHEDULING", entityType = "Schedule")
    public void deleteSchedule(Long scheduleId, Long currentUserId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", scheduleId));

        scheduleRepository.delete(schedule);
    }

    @Transactional(readOnly = true)
    public List<User> getAllStaff() {
        return userRepository.findByActiveTrue();
    }

    private void markCompletedSchedules(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate currentDate = now.toLocalDate();
        LocalTime currentTime = now.toLocalTime();

        List<Schedule> pastDueSchedules = scheduleRepository
                .findPastDueActiveByCreator(userId, currentDate, currentTime);

        if (!pastDueSchedules.isEmpty()) {
            pastDueSchedules.forEach(s -> s.setStatus(ScheduleStatus.COMPLETED));
            scheduleRepository.saveAll(pastDueSchedules);
        }
    }

    private ScheduleResponse toResponse(Schedule schedule) {
        return new ScheduleResponse(
                schedule.getId(),
                schedule.getScheduleType().name(),
                schedule.getTitle(),
                schedule.getScheduleDate(),
                schedule.getScheduleTime(),
                schedule.getEndTime(),
                schedule.getLocation(),
                schedule.getStatus().name(),
                schedule.getPriority().name(),
                schedule.getStaff().getId(),
                schedule.getStaff().getName(),
                schedule.getCreatedByUser().getId(),
                schedule.getCreatedByUser().getName(),
                schedule.getCreatedAt()
        );
    }
}
