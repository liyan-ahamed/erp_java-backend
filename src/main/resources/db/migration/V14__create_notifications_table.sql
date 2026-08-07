-- =============================================
-- V14__create_notifications_table.sql
-- Creates a generic notification table for the notification service.
-- =============================================

CREATE TABLE notifications (
    id              BIGSERIAL    PRIMARY KEY,
    recipient_id    BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type            VARCHAR(30)  NOT NULL,
    title           VARCHAR(255) NOT NULL,
    message         TEXT         NOT NULL,
    module          VARCHAR(50),
    reference_id    BIGINT,
    reference_type  VARCHAR(50),
    priority        VARCHAR(20)  NOT NULL DEFAULT 'NORMAL',
    is_read         BOOLEAN      NOT NULL DEFAULT FALSE,
    read_at         TIMESTAMP,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Indexes for common query patterns
CREATE INDEX idx_notifications_recipient_read ON notifications(recipient_id, is_read);
CREATE INDEX idx_notifications_created_at     ON notifications(created_at);
CREATE INDEX idx_notifications_type           ON notifications(type);
