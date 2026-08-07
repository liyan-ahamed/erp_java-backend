package com.java.erp.modules.schedule.repository;

import com.java.erp.modules.schedule.entity.ScheduleParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleParticipantRepository extends JpaRepository<ScheduleParticipant, Long> {
    
    List<ScheduleParticipant> findByScheduleId(Long scheduleId);
    
    List<ScheduleParticipant> findByUserId(Long userId);
    
    void deleteByScheduleId(Long scheduleId);
}
