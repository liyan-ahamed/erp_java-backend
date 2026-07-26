package com.java.erp.modules.schedule.repository;

import com.java.erp.modules.schedule.entity.Schedule;
import com.java.erp.modules.schedule.entity.ScheduleType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Repository for Schedule entity.
 * Uses JPQL with optional type filter to minimize duplicate methods.
 */
@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    /**
     * Find schedules created by a specific HOD, with optional schedule type filter.
     *
     * @param userId the creator's user ID
     * @param type   optional schedule type filter (null to return all types)
     * @return schedules ordered by date and time descending
     */
    @Query("SELECT s FROM Schedule s " +
           "WHERE s.createdByUser.id = :userId " +
           "AND (:type IS NULL OR s.scheduleType = :type) " +
           "ORDER BY s.scheduleDate DESC, s.scheduleTime DESC")
    List<Schedule> findByCreatorAndOptionalType(@Param("userId") Long userId,
                                                @Param("type") ScheduleType type);

    /**
     * Find schedules assigned to a specific staff member, with optional schedule type filter.
     *
     * @param staffId the assigned staff member's user ID
     * @param type    optional schedule type filter (null to return all types)
     * @return schedules ordered by date and time descending
     */
    @Query("SELECT s FROM Schedule s " +
           "WHERE s.staff.id = :staffId " +
           "AND (:type IS NULL OR s.scheduleType = :type) " +
           "ORDER BY s.scheduleDate DESC, s.scheduleTime DESC")
    List<Schedule> findByStaffAndOptionalType(@Param("staffId") Long staffId,
                                              @Param("type") ScheduleType type);

    /**
     * Find active (not completed) schedules whose scheduled date/time has passed.
     * Used for lazy completion marking on HOD GET requests.
     *
     * @param userId      the creator's user ID
     * @param currentDate current server date
     * @param currentTime current server time
     * @return past-due active schedules
     */
    @Query("SELECT s FROM Schedule s " +
           "WHERE s.createdByUser.id = :userId " +
           "AND s.completed = false " +
           "AND (s.scheduleDate < :currentDate " +
           "     OR (s.scheduleDate = :currentDate AND s.scheduleTime < :currentTime))")
    List<Schedule> findPastDueActiveByCreator(@Param("userId") Long userId,
                                              @Param("currentDate") LocalDate currentDate,
                                              @Param("currentTime") LocalTime currentTime);

    /**
     * Find active (not completed) schedules created by a specific HOD,
     * with optional schedule type filter.
     *
     * @param userId the creator's user ID
     * @param type   optional schedule type filter (null to return all types)
     * @return active schedules ordered by date and time descending
     */
    @Query("SELECT s FROM Schedule s " +
           "WHERE s.createdByUser.id = :userId " +
           "AND s.completed = false " +
           "AND (:type IS NULL OR s.scheduleType = :type) " +
           "ORDER BY s.scheduleDate DESC, s.scheduleTime DESC")
    List<Schedule> findActiveByCreatorAndOptionalType(@Param("userId") Long userId,
                                                      @Param("type") ScheduleType type);

    /**
     * Find completed schedules created by a specific HOD,
     * with optional schedule type filter.
     * Uses Pageable to limit results (latest 25).
     *
     * @param userId   the creator's user ID
     * @param type     optional schedule type filter (null to return all types)
     * @param pageable pageable for limiting results
     * @return completed schedules ordered by date and time descending
     */
    @Query("SELECT s FROM Schedule s " +
           "WHERE s.createdByUser.id = :userId " +
           "AND s.completed = true " +
           "AND (:type IS NULL OR s.scheduleType = :type) " +
           "ORDER BY s.scheduleDate DESC, s.scheduleTime DESC")
    List<Schedule> findCompletedByCreatorAndOptionalType(@Param("userId") Long userId,
                                                         @Param("type") ScheduleType type,
                                                         Pageable pageable);
}
