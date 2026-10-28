-- Migration: Add transformations table
-- Version: 4
-- Description: Adding the transformations table that will hold data about which image has which transformation applied.

SET SCHEMA 'app';

CREATE TABLE transformations (
    id uuid primary key default gen_random_uuid(),
    user_id bigint not null references users(id) on delete cascade,
    source_image_id uuid not null references images(id) on delete cascade,
    result_image_id uuid references images(id) on delete cascade,
    transform_hash varchar(64) not null,
    created_at timestamp not null default now(),
    unique (source_image_id, transform_hash)
);

create index on transformations(source_image_id);
create index on transformations(user_id, created_at desc);