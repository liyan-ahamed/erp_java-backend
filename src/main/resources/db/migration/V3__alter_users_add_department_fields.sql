-- =============================================
-- V3__alter_users_add_department_fields.sql
-- Evolves the users table for the department ERP:
--   1. Merges first_name + last_name into a single 'name' column
--   2. Adds staff_code, designation, and role (HOD/STAFF)
-- =============================================

-- Step 1: Add the 'name' column (nullable temporarily for data migration)
ALTER TABLE users ADD COLUMN IF NOT EXISTS name VARCHAR(100);

-- Step 2: Populate 'name' from existing first_name + last_name
UPDATE users SET name = TRIM(COALESCE(first_name, '') || ' ' || COALESCE(last_name, ''))
WHERE name IS NULL;

-- Step 3: Make 'name' NOT NULL now that data is migrated
ALTER TABLE users ALTER COLUMN name SET NOT NULL;

-- Step 4: Drop the old first_name and last_name columns
ALTER TABLE users DROP COLUMN IF EXISTS first_name;
ALTER TABLE users DROP COLUMN IF EXISTS last_name;

-- Step 5: Add staff_code (unique identifier for department staff)
ALTER TABLE users ADD COLUMN IF NOT EXISTS staff_code VARCHAR(20) UNIQUE;

-- Step 6: Add designation (e.g. 'Assistant Professor', 'Professor')
ALTER TABLE users ADD COLUMN IF NOT EXISTS designation VARCHAR(50);

-- Step 7: Create a PostgreSQL enum type for department role
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'dept_role') THEN
        CREATE TYPE dept_role AS ENUM ('HOD', 'STAFF');
    END IF;
END
$$;

-- Step 8: Add department role column with default 'STAFF'
ALTER TABLE users ADD COLUMN IF NOT EXISTS role dept_role NOT NULL DEFAULT 'STAFF';

-- Step 9: Index on staff_code for fast lookups
CREATE INDEX IF NOT EXISTS idx_users_staff_code ON users(staff_code);
