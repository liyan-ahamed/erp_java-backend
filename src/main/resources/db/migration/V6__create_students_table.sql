-- =============================================
-- V6__create_students_table.sql
-- Creates the students table.
-- FK dependencies: batches, sections
-- =============================================

CREATE TABLE students (
    id               BIGSERIAL    PRIMARY KEY,
    register_number  VARCHAR(20)  NOT NULL UNIQUE,
    name             VARCHAR(100) NOT NULL,
    dob              DATE         NOT NULL,
    gender           VARCHAR(10),
    batch_id         BIGINT       NOT NULL REFERENCES batches(id),
    section_id       BIGINT       NOT NULL REFERENCES sections(id),
    email            VARCHAR(100),
    phone            VARCHAR(15),
    parent_name      VARCHAR(100),
    parent_phone     VARCHAR(15),
    address          TEXT,
    blood_group      VARCHAR(5),
    admission_date   DATE,
    photo_url        VARCHAR(255),
    is_active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Composite index for batch+section queries (class lists)
CREATE INDEX idx_students_batch_section ON students(batch_id, section_id);

-- Index on register_number (already unique, but explicit for clarity)
CREATE INDEX idx_students_register_number ON students(register_number);
