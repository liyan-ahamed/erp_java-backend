package com.java.erp.modules.schedule.service;

import com.java.erp.exception.BadRequestException;
import com.java.erp.exception.ResourceNotFoundException;
import com.java.erp.exception.UnauthorizedException;
import com.java.erp.modules.auth.entity.User;
import com.java.erp.modules.auth.repository.UserRepository;
import com.java.erp.modules.schedule.dto.request.CreateScheduleRequest;
import com.java.erp.modules.schedule.dto.response.HodScheduleResponse;
import com.java.erp.modules.schedule.dto.response.ScheduleResponse;
import com.java.erp.modules.schedule.entity.Schedule;
import com.java.erp.modules.schedule.entity.ScheduleType;
import com.java.erp.modules.schedule.repository.ScheduleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for schedule management operations.
 * Handles creation, retrieval, and deletion of schedules with role-based access.
 */
@Service
public class ScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleService.class);

    private static final int COMPLETED_SCHEDULES_LIMIT = 25;

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public ScheduleService(ScheduleRepository scheduleRepository,
                           UserRepository userRepository) {
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create schedule(s) for the given staff members.
     * One schedule record is created per staff ID.
     *
     * @param request       the schedule creation request
     * @param currentUserId the authenticated HOD's user ID
     * @return list of created schedule responses
     */
    @Transactional
    public List<ScheduleResponse> createSchedules(CreateScheduleRequest request, Long currentUserId) {
        User creator = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UnauthorizedException("Authenticated user not found"));

        // Parse and validate schedule type
        ScheduleType scheduleType;
        try {
            scheduleType = ScheduleType.valueOf(request.getScheduleType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid schedule type: " + request.getScheduleType()
                    + ". Allowed values: DEADLINE, MEETING");
        }

        // Validate and fetch all staff members
        List<User> staffMembers = new ArrayList<>();
        for (Long staffId : request.getStaffIds()) {
            User staff = userRepository.findById(staffId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", staffId));
            staffMembers.add(staff);
        }

        // Create one schedule per staff member
        List<Schedule> schedules = new ArrayList<>();
        for (User staff : staffMembers) {
            Schedule schedule = new Schedule();
            schedule.setStaff(staff);
            schedule.setCreatedByUser(creator);
            schedule.setScheduleType(scheduleType);
            schedule.setTitle(request.getTitle());
            schedule.setScheduleDate(request.getDate());
            schedule.setScheduleTime(request.getTime());
            schedules.add(schedule);
        }

        List<Schedule> saved = scheduleRepository.saveAll(schedules);
        logger.info("Created {} schedule(s) by user '{}'", saved.size(), currentUserId);

        return saved.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get schedules based on the authenticated user's role.
     * HOD sees active schedules they created plus the latest 25 completed schedules.
     * Staff sees all schedules assigned to them (unchanged).
     *
     * @param type          optional schedule type filter
     * @param currentUserId the authenticated user's ID
     * @param isHod         true if the user has HOD role
     * @return list of schedule responses
     */
    @Transactional
    public List<ScheduleResponse> getSchedules(ScheduleType type, Long currentUserId, boolean isHod) {
        if (isHod) {
            // Mark past-due schedules as completed (type-independent)
            markCompletedSchedules(currentUserId);

            // Fetch active schedules (with optional type filter)
            List<Schedule> activeSchedules = scheduleRepository
                    .findActiveByCreatorAndOptionalType(currentUserId, type);

            // Fetch latest 25 completed schedules (with optional type filter)
            List<Schedule> completedSchedules = scheduleRepository
                    .findCompletedByCreatorAndOptionalType(currentUserId, type,
                            PageRequest.of(0, COMPLETED_SCHEDULES_LIMIT));

            // Combine: active first, then completed
            List<ScheduleResponse> response = new ArrayList<>();
            activeSchedules.stream().map(this::toHodResponse).forEach(response::add);
            completedSchedules.stream().map(this::toHodResponse).forEach(response::add);
            return response;
        } else {
            // Staff view: unchanged — return all assigned schedules
            List<Schedule> schedules = scheduleRepository
                    .findByStaffAndOptionalType(currentUserId, type);
            return schedules.stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Delete a schedule. Only the creator (HOD) can delete their own schedules.
     *
     * @param scheduleId    the schedule ID to delete
     * @param currentUserId the authenticated HOD's user ID
     */
    @Transactional
    public void deleteSchedule(Long scheduleId, Long currentUserId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", scheduleId));

        if (!schedule.getCreatedByUser().getId().equals(currentUserId)) {
            throw new UnauthorizedException("You can only delete schedules you created");
        }

        scheduleRepository.delete(schedule);
        logger.info("Deleted schedule '{}' by user '{}'", scheduleId, currentUserId);
    }

    /**
     * Get all active staff members.
     * Reuses UserRepository.findByActiveTrue().
     *
     * @return list of active users
     */
    @Transactional(readOnly = true)
    public List<User> getAllStaff() {
        return userRepository.findByActiveTrue();
    }

    /**
     * Mark past-due active schedules as completed.
     * Fetches only schedules where completed = false and scheduled date/time has passed,
     * then sets completed = true and saves in bulk.
     *
     * @param userId the HOD's user ID
     */
    private void markCompletedSchedules(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate currentDate = now.toLocalDate();
        LocalTime currentTime = now.toLocalTime();

        List<Schedule> pastDueSchedules = scheduleRepository
                .findPastDueActiveByCreator(userId, currentDate, currentTime);

        if (!pastDueSchedules.isEmpty()) {
            pastDueSchedules.forEach(s -> s.setCompleted(true));
            scheduleRepository.saveAll(pastDueSchedules);
            logger.info("Marked {} schedule(s) as completed for user '{}'",
                    pastDueSchedules.size(), userId);
        }
    }

    /**
     * Map a Schedule entity to a ScheduleResponse DTO (staff view).
     * Does not include the completed field.
     */
    private ScheduleResponse toResponse(Schedule schedule) {
        return new ScheduleResponse(
                schedule.getId(),
                schedule.getScheduleType().name(),
                schedule.getTitle(),
                schedule.getScheduleDate(),
                schedule.getScheduleTime(),
                schedule.getStaff().getId(),
                schedule.getStaff().getName(),
                schedule.getCreatedByUser().getId(),
                schedule.getCreatedByUser().getName(),
                schedule.getCreatedAt()
        );
    }

    /**
     * Map a Schedule entity to a HodScheduleResponse DTO (HOD view).
     * Includes the completed field.
     */
    private HodScheduleResponse toHodResponse(Schedule schedule) {
        return new HodScheduleResponse(
                schedule.getId(),
                schedule.getScheduleType().name(),
                schedule.getTitle(),
                schedule.getScheduleDate(),
                schedule.getScheduleTime(),
                schedule.getStaff().getId(),
                schedule.getStaff().getName(),
                schedule.getCreatedByUser().getId(),
                schedule.getCreatedByUser().getName(),
                schedule.getCreatedAt(),
                schedule.isCompleted()
        );
    }
}
