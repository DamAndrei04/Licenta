-- ========================================
-- Migration: Change ID from INTEGER to BIGINT (LONG)
-- ========================================

ALTER TABLE users ALTER COLUMN id TYPE BIGINT;
ALTER SEQUENCE IF EXISTS users_id_seq AS BIGINT;

ALTER TABLE projects ALTER COLUMN id TYPE BIGINT;
ALTER SEQUENCE IF EXISTS projects_id_seq AS BIGINT;

ALTER TABLE pages ALTER COLUMN id TYPE BIGINT;
ALTER SEQUENCE IF EXISTS pages_id_seq AS BIGINT;

ALTER TABLE components ALTER COLUMN id TYPE BIGINT;
ALTER SEQUENCE IF EXISTS components_id_seq AS BIGINT;

