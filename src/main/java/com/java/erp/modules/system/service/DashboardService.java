package com.java.erp.modules.system.service;

import com.java.erp.modules.auth.repository.UserRepository;
import com.java.erp.modules.system.dto.response.DashboardSummaryResponse;
import com.java.erp.modules.system.dto.response.DashboardSummaryResponse.SectionWiseStudentCount;
import com.java.erp.modules.system.dto.response.DashboardSummaryResponse.YearWiseStudentCount;
import com.java.erp.modules.system.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for aggregating dashboard summary data.
 * Reuses existing Student and User repositories.
 */
@Service
public class DashboardService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    public DashboardService(StudentRepository studentRepository,
                            UserRepository userRepository) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }

    /**
     * Build the complete dashboard summary.
     *
     * @return DashboardSummaryResponse with totals and breakdowns
     */
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getDashboardSummary() {
        long totalStudents = studentRepository.countByActiveTrue();
        long totalStaff = userRepository.countByActiveTrue();

        List<YearWiseStudentCount> yearWise = studentRepository.countStudentsByYear()
                .stream()
                .map(row -> new YearWiseStudentCount(row.getYear(), row.getStudentCount()))
                .collect(Collectors.toList());

        List<SectionWiseStudentCount> sectionWise = studentRepository.countStudentsBySection()
                .stream()
                .map(row -> new SectionWiseStudentCount(
                        row.getSectionId(),
                        row.getSectionName(),
                        row.getBatchName(),
                        row.getStudentCount()))
                .collect(Collectors.toList());

        return new DashboardSummaryResponse(totalStudents, totalStaff, yearWise, sectionWise);
    }
}
