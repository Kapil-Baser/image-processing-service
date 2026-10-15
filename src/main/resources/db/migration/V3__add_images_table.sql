-- Migration: Add images table
-- Version: 3
-- Description: Adding the images table

SET SCHEMA 'app';

CREATE TABLE images (
    id uuid primary key default gen_random_uuid(),
    user_id bigint not null references users(id) on delete cascade,
    storage_path text not null unique,
    original_name text,
    mime_type varchar(100) not null,
    size_bytes bigint not null,
    width int,
    height int,
    created_at timestamp not null default now()
);

create index on images (user_id, created_at desc);


