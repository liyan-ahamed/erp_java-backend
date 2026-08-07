-- =============================================
-- V11__add_audit_columns_to_roles.sql
-- Adds created_by and updated_by columns to roles table
-- =============================================

ALTER TABLE roles ADD COLUMN IF NOT EXISTS created_by VARCHAR(255);
ALTER TABLE roles ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);
