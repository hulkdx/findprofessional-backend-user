--liquibase formatted sql

--changeset saba:1
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
  updated_at      timestamptz NOT NULL
);

CREATE UNIQUE INDEX ON professional_review (user_id, professional_id);

CREATE TABLE professional_availability (
  id              BIGSERIAL PRIMARY KEY,
  professional_id BIGINT NOT NULL REFERENCES professionals (id) ON DELETE CASCADE,
  availability    TSTZRANGE NOT NULL,
  created_at      timestamptz NOT NULL,
  updated_at      timestamptz NOT NULL
);


CREATE EXTENSION IF NOT EXISTS btree_gist;

ALTER TABLE professional_availability
  ADD CONSTRAINT no_overlapping_times
  EXCLUDE USING gist (
    professional_id WITH =,
    availability WITH &&
  );

CREATE TABLE bookings (
  id                BIGSERIAL PRIMARY KEY,
  user_id           BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
  status            VARCHAR(50) NOT NULL,
  amount_in_cents   BIGINT NOT NULL,
  currency          VARCHAR(10) NOT NULL,
  payment_intent_id VARCHAR(255),
  idempotency_key   VARCHAR(255) NOT NULL,
  created_at        timestamptz NOT NULL,
  updated_at        timestamptz NOT NULL
);

-- Temporary table for TTL in bookings
CREATE TABLE booking_holds (
  id                BIGSERIAL PRIMARY KEY,
  user_id           BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
  availability_id   BIGINT NOT NULL REFERENCES professional_availability (id) ON DELETE CASCADE,
  created_at        timestamptz NOT NULL,
  expires_at        timestamptz NOT NULL
);

-- Same as booking_holds for audit purposes / etc for booking
CREATE TABLE booking_professional_availability (
  id                BIGSERIAL PRIMARY KEY,
  availability_id   BIGINT NOT NULL REFERENCES professional_availability (id) ON DELETE CASCADE,
  booking_id        BIGINT NOT NULL REFERENCES bookings (id) ON DELETE CASCADE,
  created_at        timestamptz NOT NULL
);
