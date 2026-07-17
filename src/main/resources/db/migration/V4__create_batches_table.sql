-- =============================================
-- V4__create_batches_table.sql
-- Creates the batches table for academic year groups.
-- =============================================

CREATE TABLE batches (
    id               BIGSERIAL    PRIMARY KEY,
    batch_name       VARCHAR(20)  NOT NULL UNIQUE,
    admission_year   INT          NOT NULL,
    graduation_year  INT          NOT NULL,
    current_year     SMALLINT     NOT NULL,
    is_active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);
