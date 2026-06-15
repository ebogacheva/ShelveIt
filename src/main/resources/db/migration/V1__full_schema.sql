CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE storages (
    id        BIGSERIAL PRIMARY KEY,
    name      VARCHAR(255) NOT NULL,
    type      VARCHAR(255),
    parent_id BIGINT REFERENCES storages (id)
);

CREATE TABLE items (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    storage_id BIGINT NOT NULL REFERENCES storages (id),
    attributes JSONB,
    embedding  vector(1536)
);

CREATE TABLE item_keywords (
    item_id BIGINT NOT NULL REFERENCES items (id),
    keyword VARCHAR(255)
);
