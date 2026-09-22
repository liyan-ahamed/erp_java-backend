-- Add the Student role without changing the existing HOD and Staff roles.
ALTER TYPE dept_role ADD VALUE IF NOT EXISTS 'STUDENT';

INSERT INTO roles (name, description, created_at, updated_at)
VALUES ('ROLE_STUDENT', 'Student', NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r CROSS JOIN permissions p
WHERE r.name = 'ROLE_STUDENT' AND p.name IN ('VIEW_SCHEDULE', 'VIEW_DASHBOARD')
ON CONFLICT DO NOTHING;

-- Staff now create student-facing polls/deadlines; HOD retains read-only access.
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r CROSS JOIN permissions p
WHERE r.name = 'ROLE_STAFF' AND p.name IN ('CREATE_SCHEDULE', 'EDIT_SCHEDULE', 'DELETE_SCHEDULE')
ON CONFLICT DO NOTHING;

ALTER TABLE students ADD COLUMN IF NOT EXISTS user_id BIGINT UNIQUE REFERENCES users(id) ON DELETE SET NULL;

CREATE TABLE student_schedules (
    id BIGSERIAL PRIMARY KEY,
    created_by BIGINT NOT NULL REFERENCES users(id),
    type VARCHAR(20) NOT NULL CHECK (type IN ('POLL', 'DEADLINE')),
    title VARCHAR(255) NOT NULL,
    details TEXT,
    due_date DATE,
    due_time TIME,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE student_schedule_sections (
    schedule_id BIGINT NOT NULL REFERENCES student_schedules(id) ON DELETE CASCADE,
    section_id BIGINT NOT NULL REFERENCES sections(id) ON DELETE CASCADE,
    PRIMARY KEY (schedule_id, section_id)
);

CREATE TABLE student_schedule_students (
    schedule_id BIGINT NOT NULL REFERENCES student_schedules(id) ON DELETE CASCADE,
    student_id BIGINT NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    PRIMARY KEY (schedule_id, student_id)
);

CREATE TABLE poll_options (
    schedule_id BIGINT NOT NULL REFERENCES student_schedules(id) ON DELETE CASCADE,
    option_text VARCHAR(255) NOT NULL,
    display_order INTEGER NOT NULL,
    PRIMARY KEY (schedule_id, display_order)
);

CREATE INDEX idx_student_schedules_created_by ON student_schedules(created_by);
CREATE INDEX idx_student_schedule_sections_section ON student_schedule_sections(section_id);
CREATE INDEX idx_student_schedule_students_student ON student_schedule_students(student_id);
