--liquibase formatted sql

--changeset saba:1
CREATE TABLE "professionals" (
  "id" BIGSERIAL PRIMARY KEY,
  "email" VARCHAR(255) UNIQUE NOT NULL,
  "password" VARCHAR(255) NOT NULL,
  "first_name" VARCHAR(255) NOT NULL,
  "last_name" VARCHAR(255) NOT NULL,
  "coach_type" VARCHAR(255) NOT NULL,
  "price_number" BIGINT,
  "price_currency" VARCHAR(255),
  "profile_image_url" VARCHAR(255),
  "description" VARCHAR(255),
  "skype_id" VARCHAR(255),
  "pending" BOOLEAN NOT NULL,
  "created_at" timestamptz NOT NULL,
  "updated_at" timestamptz NOT NULL
);

CREATE TABLE "users" (
  "id" BIGSERIAL PRIMARY KEY,
  "professional_id" BIGINT,
  "email" VARCHAR(255) UNIQUE NOT NULL,
  "password" VARCHAR(255) NOT NULL,
  "first_name" VARCHAR(255) NOT NULL,
  "last_name" VARCHAR(255) NOT NULL,
  "profile_image" VARCHAR(255),
  "skype_id" VARCHAR(255),
  "created_at" timestamptz NOT NULL,
  "updated_at" timestamptz NOT NULL
);

ALTER TABLE "users" ADD FOREIGN KEY ("professional_id") REFERENCES "professionals" ("id") ON DELETE CASCADE;

CREATE TABLE "professional_review" (
  "id" BIGSERIAL PRIMARY KEY,
  "user_id" BIGINT NOT NULL,
  "professional_id" BIGINT NOT NULL,
  "rate" INT NOT NULL,
  "content_text" VARCHAR(255),
  "created_at" timestamptz NOT NULL,
  "updated_at" timestamptz NOT NULL
);

CREATE UNIQUE INDEX ON "professional_review" ("user_id", "professional_id");

ALTER TABLE "professional_review" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "professional_review" ADD FOREIGN KEY ("professional_id") REFERENCES "professionals" ("id");

CREATE TABLE "professional_availability" (
  "id" BIGSERIAL PRIMARY KEY,
  "professional_id" BIGINT NOT NULL,
  "availability" TSRANGE NOT NULL,
  "created_at" timestamptz NOT NULL,
  "updated_at" timestamptz NOT NULL
);

ALTER TABLE "professional_availability" ADD FOREIGN KEY ("professional_id") REFERENCES "professionals" ("id");

CREATE EXTENSION IF NOT EXISTS btree_gist;
ALTER TABLE professional_availability
  ADD CONSTRAINT no_overlapping_times
  EXCLUDE USING gist (
    professional_id WITH =,
    availability WITH &&
  );
