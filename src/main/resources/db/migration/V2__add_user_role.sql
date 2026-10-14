-- Migration: Add user role
-- Version: 2
-- Description: Adding the user role to users table

SET SCHEMA 'app';

ALTER TABLE users
    ADD COLUMN user_role varchar(20) NOT NULL DEFAULT 'USER';

ALTER TABLE users
    ADD CONSTRAINT chk_user_role
        CHECK ( user_role IN ('ADMIN', 'USER') );
