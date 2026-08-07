-- =============================================
-- V17__add_missing_indexes.sql
-- Adds missing performance indexes.
-- =============================================

-- Index on users.is_active for filtering queries
CREATE INDEX IF NOT EXISTS idx_users_is_active ON users(is_active);

-- Composite index for schedule conflict detection
CREATE INDEX IF NOT EXISTS idx_schedules_staff_date_status
    ON schedules(staff_id, schedule_date, status);
