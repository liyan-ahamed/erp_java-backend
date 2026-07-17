-- =============================================
-- seed_dummy_data.sql
-- ⚠️  DEV/TEST DATA ONLY — DO NOT RUN IN PRODUCTION ⚠️
--
-- Run this manually against your local dev database:
--   psql -h localhost -p 5432 -U postgres -d java_erp_database -f seed_dummy_data.sql
--
-- Password hashes below are PLACEHOLDERS. In a real environment,
-- generate proper bcrypt hashes using:
--   - Spring's BCryptPasswordEncoder.encode("password")
--   - Or: htpasswd -nbBC 10 "" "password" | cut -d: -f2
-- =============================================

BEGIN;

-- =============================================
-- 1. USERS (department staff)
-- =============================================
INSERT INTO users (username, email, password, name, phone, is_active, staff_code, designation, role, created_at, updated_at)
VALUES
    (
        'dr.kumar',
        'hod@dept.edu',
        '$2a$10$PLACEHOLDER_BCRYPT_HASH_FOR_HOD_PASSWORD_REPLACE_ME_000000',
        'Dr. Rajesh Kumar',
        '9876543210',
        TRUE,
        'HOD001',
        'Professor & Head',
        'HOD',
        NOW(),
        NOW()
    ),
    (
        'priya.s',
        'priya.staff@dept.edu',
        '$2a$10$PLACEHOLDER_BCRYPT_HASH_FOR_STAFF_PASSWORD_REPLACE_ME_00000',
        'Priya Shanmugam',
        '9876543211',
        TRUE,
        'STF001',
        'Assistant Professor',
        'STAFF',
        NOW(),
        NOW()
    );

-- Assign roles to the new users (ROLE_ADMIN for HOD, ROLE_USER for STAFF)
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'hod@dept.edu' AND r.name = 'ROLE_ADMIN';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'priya.staff@dept.edu' AND r.name = 'ROLE_USER';

-- =============================================
-- 2. BATCHES (4 academic year groups)
-- =============================================
-- Current year 2026: 1st year admitted 2026, 4th year admitted 2023
INSERT INTO batches (batch_name, admission_year, graduation_year, current_year, is_active, created_at)
VALUES
    ('2026-2030', 2026, 2030, 1, TRUE, NOW()),   -- 1st year
    ('2025-2029', 2025, 2029, 2, TRUE, NOW()),   -- 2nd year
    ('2024-2028', 2024, 2028, 3, TRUE, NOW()),   -- 3rd year
    ('2023-2027', 2023, 2027, 4, TRUE, NOW());   -- 4th year

-- =============================================
-- 3. SECTIONS
-- =============================================
-- 1st year batch (6 sections: A–F)
INSERT INTO sections (batch_id, section_name, class_advisor_id, created_at)
SELECT b.id, s.section_name, NULL, NOW()
FROM batches b
CROSS JOIN (VALUES ('A'), ('B'), ('C'), ('D'), ('E'), ('F')) AS s(section_name)
WHERE b.batch_name = '2026-2030';

-- 2nd year batch (4 sections: A–D)
INSERT INTO sections (batch_id, section_name, class_advisor_id, created_at)
SELECT b.id, s.section_name, NULL, NOW()
FROM batches b
CROSS JOIN (VALUES ('A'), ('B'), ('C'), ('D')) AS s(section_name)
WHERE b.batch_name = '2025-2029';

-- 3rd year batch (4 sections: A–D)
INSERT INTO sections (batch_id, section_name, class_advisor_id, created_at)
SELECT b.id, s.section_name, NULL, NOW()
FROM batches b
CROSS JOIN (VALUES ('A'), ('B'), ('C'), ('D')) AS s(section_name)
WHERE b.batch_name = '2024-2028';

-- 4th year batch (2 sections: A–B)
INSERT INTO sections (batch_id, section_name, class_advisor_id, created_at)
SELECT b.id, s.section_name, NULL, NOW()
FROM batches b
CROSS JOIN (VALUES ('A'), ('B')) AS s(section_name)
WHERE b.batch_name = '2023-2027';

-- Assign Priya as class advisor for 1st year Section A
UPDATE sections
SET class_advisor_id = (SELECT id FROM users WHERE email = 'priya.staff@dept.edu')
WHERE batch_id = (SELECT id FROM batches WHERE batch_name = '2026-2030')
  AND section_name = 'A';

-- =============================================
-- 4. STUDENTS (6 sample students across sections)
-- =============================================
INSERT INTO students (
    register_number, name, dob, gender, batch_id, section_id,
    email, phone, parent_name, parent_phone, address, blood_group,
    admission_date, photo_url, is_active, created_at, updated_at
)
VALUES
    -- 1st year, Section A (2 students)
    (
        '2026CS001',
        'Arun Kumar M',
        '2008-03-15',
        'MALE',
        (SELECT id FROM batches WHERE batch_name = '2026-2030'),
        (SELECT id FROM sections WHERE batch_id = (SELECT id FROM batches WHERE batch_name = '2026-2030') AND section_name = 'A'),
        'arun.2026cs001@student.edu',
        '9001234567',
        'Murugan K',
        '9001234560',
        '12, Gandhi Street, Chennai - 600001',
        'O+',
        '2026-07-01',
        NULL,
        TRUE,
        NOW(),
        NOW()
    ),
    (
        '2026CS002',
        'Divya R',
        '2008-06-22',
        'FEMALE',
        (SELECT id FROM batches WHERE batch_name = '2026-2030'),
        (SELECT id FROM sections WHERE batch_id = (SELECT id FROM batches WHERE batch_name = '2026-2030') AND section_name = 'A'),
        'divya.2026cs002@student.edu',
        '9001234568',
        'Ramesh S',
        '9001234561',
        '45, Nehru Nagar, Coimbatore - 641001',
        'A+',
        '2026-07-01',
        NULL,
        TRUE,
        NOW(),
        NOW()
    ),
    -- 1st year, Section B (1 student)
    (
        '2026CS015',
        'Karthik V',
        '2008-01-10',
        'MALE',
        (SELECT id FROM batches WHERE batch_name = '2026-2030'),
        (SELECT id FROM sections WHERE batch_id = (SELECT id FROM batches WHERE batch_name = '2026-2030') AND section_name = 'B'),
        'karthik.2026cs015@student.edu',
        '9001234569',
        'Venkatesh P',
        '9001234562',
        '78, Anna Salai, Madurai - 625001',
        'B+',
        '2026-07-01',
        NULL,
        TRUE,
        NOW(),
        NOW()
    ),
    -- 2nd year, Section A (1 student)
    (
        '2025CS003',
        'Meena S',
        '2007-11-05',
        'FEMALE',
        (SELECT id FROM batches WHERE batch_name = '2025-2029'),
        (SELECT id FROM sections WHERE batch_id = (SELECT id FROM batches WHERE batch_name = '2025-2029') AND section_name = 'A'),
        'meena.2025cs003@student.edu',
        '9001234570',
        'Suresh K',
        '9001234563',
        '23, MGR Road, Trichy - 620001',
        'AB+',
        '2025-07-01',
        NULL,
        TRUE,
        NOW(),
        NOW()
    ),
    -- 3rd year, Section C (1 student)
    (
        '2024CS042',
        'Vikram J',
        '2006-08-18',
        'MALE',
        (SELECT id FROM batches WHERE batch_name = '2024-2028'),
        (SELECT id FROM sections WHERE batch_id = (SELECT id FROM batches WHERE batch_name = '2024-2028') AND section_name = 'C'),
        'vikram.2024cs042@student.edu',
        '9001234571',
        'Jayaraman V',
        '9001234564',
        '56, Kamarajar Road, Salem - 636001',
        'O-',
        '2024-07-01',
        NULL,
        TRUE,
        NOW(),
        NOW()
    ),
    -- 4th year, Section A (1 student)
    (
        '2023CS010',
        'Lakshmi P',
        '2005-04-30',
        'FEMALE',
        (SELECT id FROM batches WHERE batch_name = '2023-2027'),
        (SELECT id FROM sections WHERE batch_id = (SELECT id FROM batches WHERE batch_name = '2023-2027') AND section_name = 'A'),
        'lakshmi.2023cs010@student.edu',
        '9001234572',
        'Palani M',
        '9001234565',
        '89, EVR Street, Erode - 638001',
        'A-',
        '2023-07-01',
        NULL,
        TRUE,
        NOW(),
        NOW()
    );

COMMIT;

-- =============================================
-- Verification queries (optional, uncomment to check)
-- =============================================
-- SELECT 'Users' AS table_name, COUNT(*) AS row_count FROM users
-- UNION ALL SELECT 'Batches', COUNT(*) FROM batches
-- UNION ALL SELECT 'Sections', COUNT(*) FROM sections
-- UNION ALL SELECT 'Students', COUNT(*) FROM students;
