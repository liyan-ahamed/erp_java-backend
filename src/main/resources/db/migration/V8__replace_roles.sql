-- =============================================
-- V8__replace_roles.sql
-- Replace generic roles with domain-specific roles (HOD, STAFF)
-- =============================================

-- 1. Insert new roles
INSERT INTO roles (name, description, created_at, updated_at) 
VALUES 
    ('ROLE_HOD', 'Head of Department', NOW(), NOW()),
    ('ROLE_STAFF', 'Staff Member', NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

-- 2. Update user_roles to map old roles to new roles
-- We need to assign ROLE_HOD to users who had ROLE_ADMIN or ROLE_SUPER_ADMIN
INSERT INTO user_roles (user_id, role_id)
SELECT DISTINCT ur.user_id, r_new.id
FROM user_roles ur
JOIN roles r_old ON ur.role_id = r_old.id
CROSS JOIN roles r_new
WHERE r_old.name IN ('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')
  AND r_new.name = 'ROLE_HOD'
ON CONFLICT DO NOTHING;

-- Assign ROLE_STAFF to users who had ROLE_USER
INSERT INTO user_roles (user_id, role_id)
SELECT DISTINCT ur.user_id, r_new.id
FROM user_roles ur
JOIN roles r_old ON ur.role_id = r_old.id
CROSS JOIN roles r_new
WHERE r_old.name = 'ROLE_USER'
  AND r_new.name = 'ROLE_STAFF'
ON CONFLICT DO NOTHING;

-- 3. Delete old user_roles links
DELETE FROM user_roles
WHERE role_id IN (
    SELECT id FROM roles WHERE name IN ('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_USER')
);

-- 4. Delete old roles
DELETE FROM roles 
WHERE name IN ('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_USER');
