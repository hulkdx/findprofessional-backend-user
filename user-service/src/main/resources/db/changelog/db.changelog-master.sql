--liquibase formatted sql

--changeset saba:1
CREATE TABLE "users" (
  "id"  BIGSERIAL PRIMARY KEY,
  "email" VARCHAR(255) UNIQUE NOT NULL,
  "password" VARCHAR(255) NOT NULL,
  "first_name" VARCHAR(255),
  "last_name" VARCHAR(255),
  "created_at" timestamp,
  "updated_at" timestamp
);

CREATE TABLE "professionals" (
  "id" BIGSERIAL PRIMARY KEY,
  "email" VARCHAR(255) UNIQUE NOT NULL,
  "password" VARCHAR(255) NOT NULL,
  "first_name" VARCHAR(255),
  "last_name" VARCHAR(255),
  "coach_type" VARCHAR(255),
  "price_number" BIGINT,
  "price_currency" VARCHAR(255),
  "profile_image_url" VARCHAR(255),
  "created_at" timestamp,
  "updated_at" timestamp
);

CREATE TABLE "professional_rating" (
  "id" BIGSERIAL PRIMARY KEY,
  "user_id" BIGINT NOT NULL,
  "professional_id" BIGINT NOT NULL,
  "rate" INT NOT NULL
);

CREATE UNIQUE INDEX ON "professional_rating" ("user_id", "professional_id");

ALTER TABLE "professional_rating" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "professional_rating" ADD FOREIGN KEY ("professional_id") REFERENCES "professionals" ("id");

