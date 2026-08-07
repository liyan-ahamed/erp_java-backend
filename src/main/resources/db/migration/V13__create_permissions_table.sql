-- =============================================
-- V13__create_permissions_table.sql
-- Creates the permissions table, role_permissions join table,
-- and seeds enterprise RBAC permissions.
-- =============================================

-- Permissions table
CREATE TABLE permissions (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    module      VARCHAR(50)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Role-Permissions join table (Many-to-Many)
CREATE TABLE role_permissions (
    role_id       BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

-- Indexes
CREATE INDEX idx_permissions_name   ON permissions(name);
CREATE INDEX idx_permissions_module ON permissions(module);

-- Seed permissions
INSERT INTO permissions (name, description, module) VALUES
    -- Scheduling
    ('CREATE_SCHEDULE',      'Create new schedules',              'SCHEDULING'),
    ('EDIT_SCHEDULE',        'Edit existing schedules',           'SCHEDULING'),
    ('DELETE_SCHEDULE',      'Delete schedules',                  'SCHEDULING'),
    ('VIEW_SCHEDULE',        'View schedules',                    'SCHEDULING'),
    -- User Management
    ('MANAGE_USERS',         'Create, update, deactivate users',  'USER_MANAGEMENT'),
    ('VIEW_USERS',           'View user list and details',        'USER_MANAGEMENT'),
    -- Reports & Dashboard
    ('VIEW_REPORTS',         'View reports',                      'REPORTS'),
    ('VIEW_DASHBOARD',       'View dashboard summary',            'DASHBOARD'),
    -- Notifications
    ('MANAGE_NOTIFICATIONS', 'Manage system notifications',       'NOTIFICATIONS'),
    -- Audit
    ('VIEW_AUDIT_LOGS',      'View audit logs',                   'AUDIT'),
    -- Approvals
    ('APPROVE_REQUESTS',     'Approve pending requests',          'APPROVALS');

-- Assign ALL permissions to ROLE_HOD
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'ROLE_HOD';

-- Assign limited permissions to ROLE_STAFF
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'ROLE_STAFF'
  AND p.name IN ('VIEW_SCHEDULE', 'VIEW_USERS', 'VIEW_REPORTS', 'VIEW_DASHBOARD');
