--liquibase formatted sql

--changeset saba:1
CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE professionals (
  id                BIGSERIAL PRIMARY KEY,
  email             TEXT UNIQUE NOT NULL,
  password          TEXT NOT NULL,
  first_name        TEXT NOT NULL,
  last_name         TEXT NOT NULL,
  coach_type        TEXT NOT NULL,
  price_number      BIGINT,
  price_currency    TEXT,
  profile_image_url TEXT,
  description       TEXT,
  pending           BOOLEAN NOT NULL,
  created_at        TIMESTAMPTZ NOT NULL,
  updated_at        TIMESTAMPTZ NOT NULL
);

CREATE TABLE users (
  id                 BIGSERIAL PRIMARY KEY,
  professional_id    BIGINT REFERENCES professionals (id) ON DELETE SET NULL,
  email              TEXT UNIQUE NOT NULL,
  password           TEXT NOT NULL,
  first_name         TEXT NOT NULL,
  last_name          TEXT NOT NULL,
  profile_image      TEXT,
  stripe_customer_id TEXT DEFAULT NULL,
  created_at         TIMESTAMPTZ NOT NULL,
  updated_at         TIMESTAMPTZ NOT NULL
);

CREATE TABLE professional_review (
  id              BIGSERIAL PRIMARY KEY,
  user_id         BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
  professional_id BIGINT NOT NULL REFERENCES professionals (id) ON DELETE CASCADE,
  rate            INT NOT NULL,
  content_text    TEXT,
  created_at      TIMESTAMPTZ NOT NULL,
  updated_at      TIMESTAMPTZ NOT NULL,

  CONSTRAINT professional_review_user_prof_unique UNIQUE (user_id, professional_id)
);

CREATE TABLE professional_availability (
  id              BIGSERIAL PRIMARY KEY,
  professional_id BIGINT NOT NULL REFERENCES professionals (id) ON DELETE CASCADE,
  availability    TSTZRANGE NOT NULL,
  is_active       BOOLEAN DEFAULT TRUE,
  created_at      TIMESTAMPTZ NOT NULL,
  updated_at      TIMESTAMPTZ NOT NULL,

  CONSTRAINT no_overlapping_times EXCLUDE USING gist (
    professional_id WITH =,
    availability WITH &&
  )
);

CREATE TABLE bookings (
  id                       BIGSERIAL PRIMARY KEY,
  user_id                  BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  professional_id          BIGINT NOT NULL REFERENCES professionals(id) ON DELETE CASCADE,
  status                   TEXT NOT NULL,
  total_amount_cents       BIGINT NOT NULL,
  currency                 TEXT NOT NULL,
  stripe_payment_intent_id TEXT UNIQUE,
  confirmed_at             TIMESTAMPTZ,
  canceled_at              TIMESTAMPTZ,
  failed_at                TIMESTAMPTZ,
  payment_expires_at       TIMESTAMPTZ,
  created_at               TIMESTAMPTZ NOT NULL,
  updated_at               TIMESTAMPTZ NOT NULL
);

CREATE TABLE booking_items (
  id                BIGSERIAL PRIMARY KEY,
  booking_id        BIGINT NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
  availability_id   BIGINT UNIQUE NOT NULL REFERENCES professional_availability (id) ON DELETE CASCADE,
  created_at        TIMESTAMPTZ NOT NULL,
  updated_at        TIMESTAMPTZ NOT NULL
);
