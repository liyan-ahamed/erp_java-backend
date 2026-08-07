-- =============================================
-- V15__create_audit_logs_table.sql
-- Creates the audit_logs table for comprehensive audit trail.
-- =============================================

CREATE TABLE audit_logs (
    id              BIGSERIAL    PRIMARY KEY,
    user_id         BIGINT       REFERENCES users(id) ON DELETE SET NULL,
    username        VARCHAR(100),
    action          VARCHAR(50)  NOT NULL,
    module          VARCHAR(50)  NOT NULL,
    entity_type     VARCHAR(100),
    entity_id       BIGINT,
    old_value       TEXT,
    new_value       TEXT,
    ip_address      VARCHAR(45),
    user_agent      VARCHAR(500),
    timestamp       TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Indexes for common query patterns
CREATE INDEX idx_audit_logs_user_timestamp   ON audit_logs(user_id, timestamp);
CREATE INDEX idx_audit_logs_module_entity    ON audit_logs(module, entity_type);
CREATE INDEX idx_audit_logs_action           ON audit_logs(action);
CREATE INDEX idx_audit_logs_timestamp        ON audit_logs(timestamp);
