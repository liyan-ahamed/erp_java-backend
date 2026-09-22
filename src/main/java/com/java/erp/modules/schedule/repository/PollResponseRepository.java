package com.java.erp.modules.schedule.repository;

import com.java.erp.modules.schedule.entity.PollResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface PollResponseRepository extends JpaRepository<PollResponse, Long> {
    boolean existsByScheduleIdAndStudentId(Long scheduleId, Long studentId);
    Optional<PollResponse> findByScheduleIdAndStudentId(Long scheduleId, Long studentId);
    List<PollResponse> findByScheduleId(Long scheduleId);
}
