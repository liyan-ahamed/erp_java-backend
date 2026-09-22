-- Poll answers are the single source of truth used by hourly reminders.
CREATE TABLE poll_responses (
    id BIGSERIAL PRIMARY KEY,
    schedule_id BIGINT NOT NULL REFERENCES student_schedules(id) ON DELETE CASCADE,
    student_id BIGINT NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    selected_option_index INTEGER NOT NULL,
    responded_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_poll_response_student UNIQUE (schedule_id, student_id)
);

CREATE INDEX idx_poll_responses_schedule ON poll_responses(schedule_id);
CREATE INDEX idx_poll_responses_student ON poll_responses(student_id);
