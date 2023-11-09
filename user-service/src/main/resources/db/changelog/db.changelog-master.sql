--liquibase formatted sql

--changeset saba:1
CREATE TABLE "users" (
  "id"  BIGSERIAL PRIMARY KEY,
  "email" VARCHAR(255) UNIQUE NOT NULL,
  "password" VARCHAR(255) NOT NULL,
  "first_name" VARCHAR(255) NOT NULL,
  "last_name" VARCHAR(255) NOT NULL,
  "profile_image" VARCHAR(255),
  "created_at" timestamp,
  "updated_at" timestamp
);

CREATE TABLE "professionals" (
  "id" BIGSERIAL PRIMARY KEY,
  "email" VARCHAR(255) UNIQUE NOT NULL,
  "password" VARCHAR(255) NOT NULL,
  "first_name" VARCHAR(255) NOT NULL,
  "last_name" VARCHAR(255) NOT NULL,
  "coach_type" VARCHAR(255) NOT NULL,
  "price_number" BIGINT NOT NULL,
  "price_currency" VARCHAR(255) NOT NULL,
  "profile_image_url" VARCHAR(255),
  "description" VARCHAR(255),
  "created_at" timestamp NOT NULL,
  "updated_at" timestamp NOT NULL
);

CREATE TABLE "professional_review" (
  "id" BIGSERIAL PRIMARY KEY,
  "user_id" BIGINT NOT NULL,
  "professional_id" BIGINT NOT NULL,
  "rate" INT NOT NULL,
  "content_text" VARCHAR(255),
  "created_at" timestamp NOT NULL,
  "updated_at" timestamp NOT NULL
);

CREATE UNIQUE INDEX ON "professional_rating" ("user_id", "professional_id");

ALTER TABLE "professional_rating" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "professional_rating" ADD FOREIGN KEY ("professional_id") REFERENCES "professionals" ("id");

CREATE TABLE "professional_availability" (
  "id" BIGSERIAL PRIMARY KEY,
  "professional_id" BIGINT NOT NULL,
  "date" DATE NOT NULL,
  "from" TIME NOT NULL,
  "to" TIME NOT NULL,
  "created_at" timestamp NOT NULL,
  "updated_at" timestamp NOT NULL
);

ALTER TABLE "professional_availability" ADD FOREIGN KEY ("professional_id") REFERENCES "professionals" ("id");
