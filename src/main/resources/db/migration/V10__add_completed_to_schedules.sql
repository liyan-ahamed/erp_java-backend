-- =============================================
-- V10__add_completed_to_schedules.sql
-- Add completion status column to schedules table
-- =============================================

ALTER TABLE schedules
    ADD COLUMN completed BOOLEAN NOT NULL DEFAULT FALSE;

-- Index for filtering by completion status
CREATE INDEX idx_schedules_completed ON schedules(completed);
