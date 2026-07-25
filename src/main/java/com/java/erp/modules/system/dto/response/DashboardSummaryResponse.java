package com.java.erp.modules.system.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO for the dashboard summary response.
 * Contains total counts and breakdowns by year and section.
 */
public class DashboardSummaryResponse {

    @JsonProperty("total_students")
    private long totalStudents;

    @JsonProperty("total_staff")
    private long totalStaff;

    @JsonProperty("year_wise_students")
    private List<YearWiseStudentCount> yearWiseStudents;

    @JsonProperty("section_wise_students")
    private List<SectionWiseStudentCount> sectionWiseStudents;

    public DashboardSummaryResponse() {
    }

    public DashboardSummaryResponse(long totalStudents, long totalStaff,
                                     List<YearWiseStudentCount> yearWiseStudents,
                                     List<SectionWiseStudentCount> sectionWiseStudents) {
        this.totalStudents = totalStudents;
        this.totalStaff = totalStaff;
        this.yearWiseStudents = yearWiseStudents;
        this.sectionWiseStudents = sectionWiseStudents;
    }

    // Getters and Setters

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getTotalStaff() {
        return totalStaff;
    }

    public void setTotalStaff(long totalStaff) {
        this.totalStaff = totalStaff;
    }

    public List<YearWiseStudentCount> getYearWiseStudents() {
        return yearWiseStudents;
    }

    public void setYearWiseStudents(List<YearWiseStudentCount> yearWiseStudents) {
        this.yearWiseStudents = yearWiseStudents;
    }

    public List<SectionWiseStudentCount> getSectionWiseStudents() {
        return sectionWiseStudents;
    }

    public void setSectionWiseStudents(List<SectionWiseStudentCount> sectionWiseStudents) {
        this.sectionWiseStudents = sectionWiseStudents;
    }

    /**
     * Nested DTO for year-wise student count.
     */
    public static class YearWiseStudentCount {

        private Short year;

        @JsonProperty("student_count")
        private long studentCount;

        public YearWiseStudentCount() {
        }

        public YearWiseStudentCount(Short year, long studentCount) {
            this.year = year;
            this.studentCount = studentCount;
        }

        public Short getYear() {
            return year;
        }

        public void setYear(Short year) {
            this.year = year;
        }

        public long getStudentCount() {
            return studentCount;
        }

        public void setStudentCount(long studentCount) {
            this.studentCount = studentCount;
        }
    }

    /**
     * Nested DTO for section-wise student count.
     */
    public static class SectionWiseStudentCount {

        @JsonProperty("section_id")
        private Long sectionId;

        @JsonProperty("section_name")
        private String sectionName;

        @JsonProperty("batch_name")
        private String batchName;

        @JsonProperty("student_count")
        private long studentCount;

        public SectionWiseStudentCount() {
        }

        public SectionWiseStudentCount(Long sectionId, String sectionName,
                                        String batchName, long studentCount) {
            this.sectionId = sectionId;
            this.sectionName = sectionName;
            this.batchName = batchName;
            this.studentCount = studentCount;
        }

        public Long getSectionId() {
            return sectionId;
        }

        public void setSectionId(Long sectionId) {
            this.sectionId = sectionId;
        }

        public String getSectionName() {
            return sectionName;
        }

        public void setSectionName(String sectionName) {
            this.sectionName = sectionName;
        }

        public String getBatchName() {
            return batchName;
        }

        public void setBatchName(String batchName) {
            this.batchName = batchName;
        }

        public long getStudentCount() {
            return studentCount;
        }

        public void setStudentCount(long studentCount) {
            this.studentCount = studentCount;
        }
    }
}
