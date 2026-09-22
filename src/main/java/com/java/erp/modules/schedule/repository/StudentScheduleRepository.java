package com.java.erp.modules.schedule.repository;

import com.java.erp.modules.schedule.entity.StudentSchedule;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface StudentScheduleRepository extends JpaRepository<StudentSchedule, Long> {
    List<StudentSchedule> findByTypeOrderByCreatedAtDesc(StudentSchedule.Type type);

    List<StudentSchedule> findByTypeAndCreatedByIdOrderByCreatedAtDesc(StudentSchedule.Type type, Long createdById);

    @Query("select distinct s from StudentSchedule s left join s.sections sec left join s.students st " +
           "where s.type = :type and (sec.id = :sectionId or st.id = :studentId) order by s.createdAt desc")
    List<StudentSchedule> findAssigned(@Param("type") StudentSchedule.Type type,
                                       @Param("sectionId") Long sectionId,
                                       @Param("studentId") Long studentId);
}
