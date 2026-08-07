-- =====================================================================
-- FINAL: Department ERP Database Seed Script
-- =====================================================================
-- Database schema derived from migrations V1 through V8.
--
-- Layout:
--   Year 1 (2024-2028): 4 Sections (A, B, C, D)
--   Year 2 (2023-2027): 3 Sections (A, B, C)
--   Year 3 (2022-2026): 2 Sections (A, B)
--   Year 4 (2021-2025): 1 Section  (A)
--   Total: 10 sections, ~574 students (55-60 per section), 10 new staff
--
-- Loginnable accounts remain the DataSeeder users:
--   HOD:   hod@department.com   / password123
--   Staff: staff@department.com / password123
-- =====================================================================

BEGIN;

-- =====================================================================
-- PART 1: CLEANUP
-- =====================================================================

-- Truncate all academic data (CASCADE flows: batches → sections → students)
TRUNCATE TABLE students, sections, batches RESTART IDENTITY CASCADE;

-- Remove previously SQL-seeded staff (preserves DataSeeder's HOD & Staff user)
DELETE FROM user_roles
WHERE user_id IN (SELECT id FROM users WHERE email LIKE 'staff_%@department.com');

DELETE FROM users
WHERE email LIKE 'staff_%@department.com';

-- =====================================================================
-- PART 2: INSERT 10 STAFF MEMBERS
-- =====================================================================
-- NOTE: Password hash is a structural placeholder. These staff accounts
-- exist for dashboard counts and class advisor assignments.
-- Use the DataSeeder accounts (hod@/staff@) for actual login.

INSERT INTO users (username, email, password, name, staff_code, phone, designation, role, is_active, created_at, updated_at)
VALUES
    ('prof.ananya',    'staff_ananya@department.com',    '$2a$12$4e9L7W7.Z2k0X1Y2Z3W4E5R6T7Y8U9I0O1P2Q3R4S5T6U7V8W9X0Y', 'Dr. Ananya Sen',         'ST003', '9811000001', 'Associate Professor', 'STAFF', true, NOW(), NOW()),
    ('prof.rajesh',    'staff_rajesh@department.com',    '$2a$12$4e9L7W7.Z2k0X1Y2Z3W4E5R6T7Y8U9I0O1P2Q3R4S5T6U7V8W9X0Y', 'Prof. Rajesh Kulkarni',  'ST004', '9811000002', 'Assistant Professor', 'STAFF', true, NOW(), NOW()),
    ('prof.meera',     'staff_meera@department.com',     '$2a$12$4e9L7W7.Z2k0X1Y2Z3W4E5R6T7Y8U9I0O1P2Q3R4S5T6U7V8W9X0Y', 'Prof. Meera Nair',       'ST005', '9811000003', 'Assistant Professor', 'STAFF', true, NOW(), NOW()),
    ('prof.vikram',    'staff_vikram@department.com',    '$2a$12$4e9L7W7.Z2k0X1Y2Z3W4E5R6T7Y8U9I0O1P2Q3R4S5T6U7V8W9X0Y', 'Prof. Vikram Chauhan',   'ST006', '9811000004', 'Assistant Professor', 'STAFF', true, NOW(), NOW()),
    ('prof.kavita',    'staff_kavita@department.com',    '$2a$12$4e9L7W7.Z2k0X1Y2Z3W4E5R6T7Y8U9I0O1P2Q3R4S5T6U7V8W9X0Y', 'Prof. Kavita Sharma',    'ST007', '9811000005', 'Assistant Professor', 'STAFF', true, NOW(), NOW()),
    ('prof.siddharth', 'staff_siddharth@department.com', '$2a$12$4e9L7W7.Z2k0X1Y2Z3W4E5R6T7Y8U9I0O1P2Q3R4S5T6U7V8W9X0Y', 'Prof. Siddharth Reddy',  'ST008', '9811000006', 'Assistant Professor', 'STAFF', true, NOW(), NOW()),
    ('prof.priyanka',  'staff_priyanka@department.com',  '$2a$12$4e9L7W7.Z2k0X1Y2Z3W4E5R6T7Y8U9I0O1P2Q3R4S5T6U7V8W9X0Y', 'Prof. Priyanka Iyer',    'ST009', '9811000007', 'Associate Professor', 'STAFF', true, NOW(), NOW()),
    ('prof.rahul',     'staff_rahul@department.com',     '$2a$12$4e9L7W7.Z2k0X1Y2Z3W4E5R6T7Y8U9I0O1P2Q3R4S5T6U7V8W9X0Y', 'Prof. Rahul Deshmukh',   'ST010', '9811000008', 'Assistant Professor', 'STAFF', true, NOW(), NOW()),
    ('prof.sneha',     'staff_sneha@department.com',     '$2a$12$4e9L7W7.Z2k0X1Y2Z3W4E5R6T7Y8U9I0O1P2Q3R4S5T6U7V8W9X0Y', 'Prof. Sneha Agarwal',    'ST011', '9811000009', 'Assistant Professor', 'STAFF', true, NOW(), NOW()),
    ('prof.gautam',    'staff_gautam@department.com',    '$2a$12$4e9L7W7.Z2k0X1Y2Z3W4E5R6T7Y8U9I0O1P2Q3R4S5T6U7V8W9X0Y', 'Dr. Gautam Verma',       'ST012', '9811000010', 'Associate Professor', 'STAFF', true, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- Assign ROLE_STAFF to all new staff users
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
CROSS JOIN roles r
WHERE u.email LIKE 'staff_%@department.com'
  AND r.name = 'ROLE_STAFF'
ON CONFLICT DO NOTHING;

-- =====================================================================
-- PART 3: INSERT BATCHES
-- =====================================================================

INSERT INTO batches (id, batch_name, admission_year, graduation_year, current_year, is_active, created_at)
VALUES
    (1, '2024-2028', 2024, 2028, 1, true, NOW()),
    (2, '2023-2027', 2023, 2027, 2, true, NOW()),
    (3, '2022-2026', 2022, 2026, 3, true, NOW()),
    (4, '2021-2025', 2021, 2025, 4, true, NOW());

SELECT setval('batches_id_seq', (SELECT MAX(id) FROM batches));

-- =====================================================================
-- PART 4: INSERT SECTIONS (different count per year, with advisors)
-- =====================================================================
--   Year 1: 4 sections    Year 2: 3 sections
--   Year 3: 2 sections    Year 4: 1 section

INSERT INTO sections (id, batch_id, section_name, class_advisor_id, created_at)
VALUES
    -- Year 1 (Batch 1): A, B, C, D
    ( 1, 1, 'A', (SELECT id FROM users WHERE email = 'staff_ananya@department.com'),    NOW()),
    ( 2, 1, 'B', (SELECT id FROM users WHERE email = 'staff_rajesh@department.com'),    NOW()),
    ( 3, 1, 'C', (SELECT id FROM users WHERE email = 'staff_meera@department.com'),     NOW()),
    ( 4, 1, 'D', (SELECT id FROM users WHERE email = 'staff_vikram@department.com'),    NOW()),
    -- Year 2 (Batch 2): A, B, C
    ( 5, 2, 'A', (SELECT id FROM users WHERE email = 'staff_kavita@department.com'),    NOW()),
    ( 6, 2, 'B', (SELECT id FROM users WHERE email = 'staff_siddharth@department.com'), NOW()),
    ( 7, 2, 'C', (SELECT id FROM users WHERE email = 'staff_priyanka@department.com'),  NOW()),
    -- Year 3 (Batch 3): A, B
    ( 8, 3, 'A', (SELECT id FROM users WHERE email = 'staff_rahul@department.com'),     NOW()),
    ( 9, 3, 'B', (SELECT id FROM users WHERE email = 'staff_sneha@department.com'),     NOW()),
    -- Year 4 (Batch 4): A
    (10, 4, 'A', (SELECT id FROM users WHERE email = 'staff_gautam@department.com'),    NOW());

SELECT setval('sections_id_seq', (SELECT MAX(id) FROM sections));

-- =====================================================================
-- PART 5: INSERT STUDENTS (55-60 per section, fully unique data)
-- =====================================================================
-- Name uniqueness strategy:
--   first_name = names_array[ (global_idx-1) / 30 + 1 ]  (changes every 30 students)
--   last_name  = names_array[ (global_idx-1) % 30 + 1 ]  (cycles within each 30)
--   This produces 40×30 = 1200 unique combos; we only need ~574.

WITH
male_first_names AS (
    SELECT ARRAY[
        'Aarav','Aditya','Akash','Anand','Aniket','Arjun','Bhavin','Chetan','Dev','Dhruv',
        'Ganesh','Gautam','Harish','Karan','Karthik','Kavish','Manish','Nikhil','Nitin','Pranav',
        'Rahul','Rohan','Sachin','Siddharth','Sanjay','Suraj','Varun','Vikas','Vikram','Yash',
        'Abhinav','Alok','Ayush','Deepak','Harsh','Hemant','Jatin','Kapil','Mayank','Naveen'
    ] AS arr   -- 40 names
),
female_first_names AS (
    SELECT ARRAY[
        'Aadhya','Ananya','Anushka','Bhavna','Divya','Deepika','Isha','Kavya','Kirti','Meera',
        'Neha','Nisha','Pooja','Priya','Priyanka','Radhika','Riya','Roshni','Sakshi','Sanjana',
        'Shreya','Sneha','Swati','Tanvi','Trisha','Vaishnavi','Vidya','Yashasvi','Zara','Samyuktha',
        'Aditi','Akanksha','Anjali','Archana','Bhumika','Kiran','Nandini','Pallavi','Rachna','Simran'
    ] AS arr   -- 40 names
),
parent_first_names AS (
    SELECT ARRAY[
        'Ramesh','Suresh','Mahesh','Rajesh','Dinesh','Vijay','Mukesh','Sunil','Anil','Prakash',
        'Sanjay','Ashok','Vinod','Satish','Deepak','Kishore','Mohan','Rajan','Subhash','Narayanan',
        'Bhaskar','Gopal','Harish','Jagdish','Kamlesh','Lalit','Naresh','Parveen','Raman','Sanjeev'
    ] AS arr   -- 30 names
),
last_names AS (
    SELECT ARRAY[
        'Sharma','Verma','Patel','Kumar','Singh','Gupta','Reddy','Rao','Nair','Iyer',
        'Menon','Pillai','Joshi','Kulkarni','Deshmukh','Mehta','Shah','Agarwal','Chawla','Malhotra',
        'Bhat','Choudhury','Das','Dutta','Gowda','Hegde','Jain','Kapoor','Khanna','Mahajan'
    ] AS arr   -- 30 names
),
-- Section-specific student counts (randomised 55-60)
section_caps AS (
    SELECT * FROM (VALUES
        ( 1, 58), ( 2, 56), ( 3, 60), ( 4, 55),   -- Year 1: A=58 B=56 C=60 D=55
        ( 5, 57), ( 6, 59), ( 7, 55),               -- Year 2: A=57 B=59 C=55
        ( 8, 58), ( 9, 60),                          -- Year 3: A=58 B=60
        (10, 56)                                     -- Year 4: A=56
    ) AS t(section_id, cap)
),
numbered AS (
    SELECT
        sec.id            AS section_id,
        sec.batch_id,
        b.admission_year,
        sec.section_name,
        i                 AS seq,
        ROW_NUMBER() OVER (ORDER BY sec.id, i) AS gx   -- globally unique index
    FROM sections sec
    JOIN batches b ON sec.batch_id = b.id
    JOIN section_caps sc ON sc.section_id = sec.id
    CROSS JOIN LATERAL generate_series(1, sc.cap) AS i
)
INSERT INTO students (
    register_number, name, dob, gender,
    batch_id, section_id,
    email, phone,
    parent_name, parent_phone,
    address, blood_group, admission_date,
    is_active, created_at, updated_at
)
SELECT
    -- REG2024A001  (15 chars max, fits VARCHAR(20))
    'REG' || n.admission_year || n.section_name || LPAD(n.seq::text, 3, '0'),

    -- Unique full name  (first rotates every 30 students, last cycles within 30)
    CASE WHEN n.seq % 2 = 1
        THEN (SELECT arr[((n.gx - 1) / 30) % 40 + 1] FROM male_first_names)
        ELSE (SELECT arr[((n.gx - 1) / 30) % 40 + 1] FROM female_first_names)
    END
    || ' '
    || (SELECT arr[((n.gx - 1) % 30) + 1] FROM last_names),

    -- DOB spread across the year (18 yrs before admission)
    ((n.admission_year - 18)::text || '-01-01')::date
        + (((n.gx * 11) % 365)::text || ' days')::interval,

    CASE WHEN n.seq % 2 = 1 THEN 'MALE' ELSE 'FEMALE' END,

    n.batch_id,
    n.section_id,

    -- Unique email: firstname.lastname<gx>@student.erp.com
    LOWER(
        CASE WHEN n.seq % 2 = 1
            THEN (SELECT arr[((n.gx - 1) / 30) % 40 + 1] FROM male_first_names)
            ELSE (SELECT arr[((n.gx - 1) / 30) % 40 + 1] FROM female_first_names)
        END
    )
    || '.'
    || LOWER((SELECT arr[((n.gx - 1) % 30) + 1] FROM last_names))
    || n.gx || '@student.erp.com',

    -- Student phone  (10 digits, fits VARCHAR(15))
    '98' || LPAD((70000000 + n.gx * 19)::text, 8, '0'),

    -- Parent: shares family surname
    (SELECT arr[((n.gx - 1) % 30) + 1] FROM parent_first_names)
    || ' '
    || (SELECT arr[((n.gx - 1) % 30) + 1] FROM last_names),

    -- Parent phone
    '97' || LPAD((80000000 + n.gx * 23)::text, 8, '0'),

    -- Address
    (n.seq * 7)::text || ' College Road, Block ' || ((n.gx % 8) + 1) || ', City',

    -- Blood group
    (ARRAY['O+','A+','B+','AB+','O-','A-'])[(n.gx % 6) + 1],

    -- Admission date
    (n.admission_year::text || '-08-01')::date,

    true,
    NOW(),
    NOW()
FROM numbered n;

COMMIT;

-- =====================================================================
-- VERIFICATION
-- =====================================================================

-- 1. Staff summary
SELECT 'Staff' AS category, COUNT(*) AS total
FROM users WHERE is_active = true;

-- 2. Students by year and section
SELECT
    b.current_year  AS year,
    b.batch_name,
    s.section_name,
    u.name          AS class_advisor,
    COUNT(st.id)    AS students
FROM students st
JOIN sections s ON st.section_id = s.id
JOIN batches  b ON s.batch_id   = b.id
LEFT JOIN users u ON s.class_advisor_id = u.id
GROUP BY b.current_year, b.batch_name, s.section_name, u.name
ORDER BY b.current_year, s.section_name;

-- 3. Uniqueness proof
SELECT
    COUNT(*)                    AS total_students,
    COUNT(DISTINCT name)        AS unique_names,
    COUNT(DISTINCT email)       AS unique_emails,
    COUNT(DISTINCT phone)       AS unique_phones,
    COUNT(DISTINCT register_number) AS unique_reg_numbers
FROM students;
