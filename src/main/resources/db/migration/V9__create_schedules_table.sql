-- =============================================
-- V9__create_schedules_table.sql
-- Create schedules table for HOD-assigned staff schedules
-- =============================================

CREATE TABLE schedules (
    id             BIGSERIAL    PRIMARY KEY,
    staff_id       BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_by     BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    schedule_type  VARCHAR(20)  NOT NULL,
    title          VARCHAR(255) NOT NULL,
    schedule_date  DATE         NOT NULL,
    schedule_time  TIME         NOT NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Indexes for common query patterns
CREATE INDEX idx_schedules_staff_id      ON schedules(staff_id);
CREATE INDEX idx_schedules_created_by    ON schedules(created_by);
CREATE INDEX idx_schedules_schedule_type ON schedules(schedule_type);
