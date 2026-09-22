package com.java.erp.modules.schedule.service;

import com.java.erp.modules.auth.entity.User;
import com.java.erp.modules.auth.repository.UserRepository;
import com.java.erp.modules.notification.entity.Notification;
import com.java.erp.modules.notification.service.NotificationService;
import com.java.erp.modules.schedule.entity.StudentSchedule;
import com.java.erp.modules.schedule.repository.PollResponseRepository;
import com.java.erp.modules.schedule.repository.StudentScheduleRepository;
import com.java.erp.modules.system.entity.Student;
import com.java.erp.modules.system.repository.StudentRepository;
import org.slf4j.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Component
public class PollReminderScheduler {
    private static final Logger logger = LoggerFactory.getLogger(PollReminderScheduler.class);
    private final StudentScheduleRepository schedules;
    private final PollResponseRepository responses;
    private final StudentRepository students;
    private final UserRepository users;
    private final NotificationService notifications;

    public PollReminderScheduler(StudentScheduleRepository schedules, PollResponseRepository responses,
            StudentRepository students, UserRepository users, NotificationService notifications) {
        this.schedules = schedules; this.responses = responses; this.students = students;
        this.users = users; this.notifications = notifications;
    }

    @Scheduled(fixedRateString = "${app.poll-reminder.interval-ms:3600000}",
               initialDelayString = "${app.poll-reminder.initial-delay-ms:3600000}")
    @Transactional
    public void remindStudentsWithUnansweredPolls() {
        int sent = 0;
        for (StudentSchedule poll : schedules.findByTypeOrderByCreatedAtDesc(StudentSchedule.Type.POLL)) {
            if (poll.getCreatedBy().getRoles().stream().noneMatch(role -> "ROLE_STAFF".equals(role.getName()))) continue;

            Map<Long, Student> assigned = new LinkedHashMap<>();
            poll.getStudents().forEach(student -> assigned.put(student.getId(), student));
            Set<Long> sectionIds = new HashSet<>();
            poll.getSections().forEach(section -> sectionIds.add(section.getId()));
            if (!sectionIds.isEmpty()) {
                students.findByActiveTrueOrderByNameAsc().stream()
                    .filter(student -> sectionIds.contains(student.getSection().getId()))
                    .forEach(student -> assigned.put(student.getId(), student));
            }

            for (Student student : assigned.values()) {
                if (!student.isActive() || responses.existsByScheduleIdAndStudentId(poll.getId(), student.getId())) continue;
                User recipient = student.getUser();
                if (recipient == null && student.getEmail() != null) recipient = users.findByEmail(student.getEmail()).orElse(null);
                if (recipient == null || !recipient.isActive() || recipient.getRoles().stream()
                        .noneMatch(role -> "ROLE_STUDENT".equals(role.getName()))) continue;

                // Re-check immediately before delivery so a response recorded during
                // the scan suppresses the reminder.
                if (responses.existsByScheduleIdAndStudentId(poll.getId(), student.getId())) continue;
                notifications.send(recipient.getId(), Notification.NotificationType.ACTION_REQUIRED,
                    "Unanswered Poll", "Reminder: You have an unanswered poll. Please respond.",
                    "SCHEDULING", poll.getId(), "POLL", Notification.NotificationPriority.HIGH);
                sent++;
            }
        }
        if (sent > 0) logger.info("Sent {} unanswered poll reminder notification(s)", sent);
    }
}
