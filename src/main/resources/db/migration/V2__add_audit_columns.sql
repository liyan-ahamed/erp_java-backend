-- =============================================
-- V2__add_audit_columns.sql
-- Adds audit trail columns (created_by, updated_by) to users table
-- =============================================

ALTER TABLE users ADD COLUMN IF NOT EXISTS created_by VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);
