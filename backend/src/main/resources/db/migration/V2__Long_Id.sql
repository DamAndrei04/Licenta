-- ========================================
-- Migration: Change ID from INTEGER to BIGINT (LONG)
-- ========================================

-- 1. Users
ALTER TABLE users ALTER COLUMN id TYPE BIGINT;
ALTER SEQUENCE IF EXISTS users_id_seq AS BIGINT;

-- 2. Projects
ALTER TABLE projects ALTER COLUMN id TYPE BIGINT;
ALTER TABLE projects ALTER COLUMN user_id TYPE BIGINT;
ALTER SEQUENCE IF EXISTS projects_id_seq AS BIGINT;

-- 3. Pages
ALTER TABLE pages ALTER COLUMN id TYPE BIGINT;
ALTER TABLE pages ALTER COLUMN project_id TYPE BIGINT;
ALTER SEQUENCE IF EXISTS pages_id_seq AS BIGINT;

-- 4. Components
ALTER TABLE components ALTER COLUMN id TYPE BIGINT;
ALTER TABLE components ALTER COLUMN page_id TYPE BIGINT;
ALTER TABLE components ALTER COLUMN parent_id TYPE BIGINT;
ALTER SEQUENCE IF EXISTS components_id_seq AS BIGINT;


