--liquibase formatted sql

--changeset saba:1
CREATE TABLE customer (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);
