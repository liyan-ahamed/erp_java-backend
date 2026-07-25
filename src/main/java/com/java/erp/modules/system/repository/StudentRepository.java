package com.java.erp.modules.system.repository;

import com.java.erp.modules.system.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Student entity.
 * Provides count-based queries for dashboard aggregation.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * Count students grouped by academic year (batch current_year).
     * Returns a list of projections with year and student count.
     */
    @Query("SELECT b.currentYear AS year, COUNT(s) AS studentCount " +
           "FROM Student s JOIN s.batch b " +
           "WHERE s.active = true AND b.active = true " +
           "GROUP BY b.currentYear ORDER BY b.currentYear")
    List<YearWiseCount> countStudentsByYear();

    /**
     * Count students grouped by section.
     * Returns a list of projections with section id, name, batch name, and student count.
     */
    @Query("SELECT sec.id AS sectionId, sec.sectionName AS sectionName, " +
           "b.batchName AS batchName, COUNT(s) AS studentCount " +
           "FROM Student s JOIN s.section sec JOIN s.batch b " +
           "WHERE s.active = true AND b.active = true " +
           "GROUP BY sec.id, sec.sectionName, b.batchName " +
           "ORDER BY b.batchName, sec.sectionName")
    List<SectionWiseCount> countStudentsBySection();

    /**
     * Count all active students.
     */
    long countByActiveTrue();

    /**
     * Projection interface for year-wise student count.
     */
    interface YearWiseCount {
        Short getYear();
        Long getStudentCount();
    }

    /**
     * Projection interface for section-wise student count.
     */
    interface SectionWiseCount {
        Long getSectionId();
        String getSectionName();
        String getBatchName();
        Long getStudentCount();
    }
}
