-- =============================================
-- V16__enhance_schedules_table.sql
-- Extends the schedules table for enterprise scheduling.
-- Adds description, location, status, priority, end_time,
-- recurrence_rule, agenda, meeting_notes, attachment_metadata.
-- Migrates completed boolean to status enum.
-- Creates schedule_participants table.
-- =============================================

-- Step 1: Add new columns to schedules
ALTER TABLE schedules ADD COLUMN IF NOT EXISTS description TEXT;
ALTER TABLE schedules ADD COLUMN IF NOT EXISTS location VARCHAR(255);
ALTER TABLE schedules ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'UPCOMING';
ALTER TABLE schedules ADD COLUMN IF NOT EXISTS priority VARCHAR(10) NOT NULL DEFAULT 'NORMAL';
ALTER TABLE schedules ADD COLUMN IF NOT EXISTS end_time TIME;
ALTER TABLE schedules ADD COLUMN IF NOT EXISTS recurrence_rule VARCHAR(255);
ALTER TABLE schedules ADD COLUMN IF NOT EXISTS agenda TEXT;
ALTER TABLE schedules ADD COLUMN IF NOT EXISTS meeting_notes TEXT;
ALTER TABLE schedules ADD COLUMN IF NOT EXISTS attachment_metadata TEXT;
ALTER TABLE schedules ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT NOW();

-- Step 2: Migrate completed boolean to status
UPDATE schedules SET status = 'COMPLETED' WHERE completed = true;
UPDATE schedules SET status = 'UPCOMING'  WHERE completed = false;

-- Step 3: Drop the completed column
ALTER TABLE schedules DROP COLUMN IF EXISTS completed;

-- Step 4: Create schedule_participants table
CREATE TABLE schedule_participants (
    id           BIGSERIAL    PRIMARY KEY,
    schedule_id  BIGINT       NOT NULL REFERENCES schedules(id) ON DELETE CASCADE,
    user_id      BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status       VARCHAR(20)  NOT NULL DEFAULT 'INVITED',
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_schedule_participant UNIQUE (schedule_id, user_id)
);

-- Step 5: Indexes
CREATE INDEX idx_schedules_status         ON schedules(status);
CREATE INDEX idx_schedules_priority       ON schedules(priority);
CREATE INDEX idx_schedules_date_time      ON schedules(schedule_date, schedule_time);
CREATE INDEX idx_schedule_participants_schedule ON schedule_participants(schedule_id);
CREATE INDEX idx_schedule_participants_user     ON schedule_participants(user_id);

-- Drop the old completed index since the column no longer exists
DROP INDEX IF EXISTS idx_schedules_completed;
