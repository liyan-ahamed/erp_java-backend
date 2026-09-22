package com.java.erp.modules.schedule.dto.response;

import java.util.List;

public record ScheduleAudienceResponse(List<ClassItem> classes, List<StudentItem> students) {
    public record ClassItem(Long id, String name) {}
    public record StudentItem(Long id, String name, String registerNumber, Long sectionId) {}
}
