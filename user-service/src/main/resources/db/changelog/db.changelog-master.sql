--liquibase formatted sql

--changeset saba:1
CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE professionals (
  id                BIGSERIAL PRIMARY KEY,
  email             VARCHAR(255) UNIQUE NOT NULL,
  password          VARCHAR(255) NOT NULL,
  first_name        VARCHAR(255) NOT NULL,
  last_name         VARCHAR(255) NOT NULL,
  coach_type        VARCHAR(255) NOT NULL,
  price_number      BIGINT,
  price_currency    VARCHAR(255),
  profile_image_url VARCHAR(255),
  description       VARCHAR(255),
  skype_id          VARCHAR(255),
  pending           BOOLEAN NOT NULL,
  created_at        timestamptz NOT NULL,
  updated_at        timestamptz NOT NULL
);

CREATE TABLE users (
  id              BIGSERIAL PRIMARY KEY,
  professional_id BIGINT REFERENCES professionals (id) ON DELETE SET NULL,
  email           VARCHAR(255) UNIQUE NOT NULL,
  password        VARCHAR(255) NOT NULL,
  first_name      VARCHAR(255) NOT NULL,
  last_name       VARCHAR(255) NOT NULL,
  profile_image   VARCHAR(255),
  skype_id        VARCHAR(255),
  created_at      timestamptz NOT NULL,
  updated_at      timestamptz NOT NULL
);

CREATE TABLE professional_review (
  id              BIGSERIAL PRIMARY KEY,
  user_id         BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
  professional_id BIGINT NOT NULL REFERENCES professionals (id) ON DELETE CASCADE,
  rate            INT NOT NULL,
  content_text    VARCHAR(255),
  created_at      timestamptz NOT NULL,
  updated_at      timestamptz NOT NULL,

  CONSTRAINT professional_review_user_prof_unique UNIQUE (user_id, professional_id)
);

CREATE TABLE professional_availability (
  id              BIGSERIAL PRIMARY KEY,
  professional_id BIGINT NOT NULL REFERENCES professionals (id) ON DELETE CASCADE,
  availability    TSTZRANGE NOT NULL,
  created_at      timestamptz NOT NULL,
  updated_at      timestamptz NOT NULL,

  CONSTRAINT no_overlapping_times EXCLUDE USING gist (
    professional_id WITH =,
    availability WITH &&
  )
);

CREATE TABLE booking_holds (
  id                BIGSERIAL PRIMARY KEY,
  user_id           BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
  idempotency_key   VARCHAR(255) NOT NULL,
  created_at        timestamptz NOT NULL,
  expires_at        timestamptz NOT NULL,

  CONSTRAINT booking_holds_user_ik_uk UNIQUE (user_id, idempotency_key)
);

CREATE TABLE booking_hold_items (
  id                BIGSERIAL PRIMARY KEY,
  hold_id           BIGINT NOT NULL REFERENCES booking_holds (id) ON DELETE CASCADE,
  availability_id   BIGINT UNIQUE NOT NULL REFERENCES professional_availability (id) ON DELETE CASCADE,
  created_at        timestamptz NOT NULL,
  expires_at        timestamptz NOT NULL
);
