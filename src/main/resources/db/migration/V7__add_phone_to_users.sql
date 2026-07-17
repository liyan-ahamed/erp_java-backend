-- =============================================
-- V7__add_phone_to_users.sql
-- Adds the phone column to users table.
-- This was in the JPA entity but missing from V1 schema.
-- =============================================

ALTER TABLE users ADD COLUMN IF NOT EXISTS phone VARCHAR(20);
