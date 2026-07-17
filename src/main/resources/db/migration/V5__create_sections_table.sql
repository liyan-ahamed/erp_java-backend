-- =============================================
-- V5__create_sections_table.sql
-- Creates the sections table.
-- FK dependencies: batches, users
-- =============================================

CREATE TABLE sections (
    id               BIGSERIAL    PRIMARY KEY,
    batch_id         BIGINT       NOT NULL REFERENCES batches(id) ON DELETE CASCADE,
    section_name     VARCHAR(5)   NOT NULL,
    class_advisor_id BIGINT       REFERENCES users(id) ON DELETE SET NULL,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),

    -- Each section name must be unique within a batch
    CONSTRAINT uq_sections_batch_section UNIQUE (batch_id, section_name)
);

-- Index for fast batch lookups
CREATE INDEX idx_sections_batch_id ON sections(batch_id);

-- Index for advisor lookups
CREATE INDEX idx_sections_class_advisor_id ON sections(class_advisor_id);
