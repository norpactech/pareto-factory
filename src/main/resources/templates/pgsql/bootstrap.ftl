-- ----------------------------------------------------------------------------
-- © 2025 Northern Pacific Technologies, LLC.
-- Licensed under the MIT License.
-- See LICENSE file in the project root for full license information.
-- ----------------------------------------------------------------------------

DROP SCHEMA IF EXISTS ${SCHEMA} cascade;
CREATE SCHEMA ${SCHEMA} AUTHORIZATION ${OWNER};

-- ----------------------------------------------------------------------------
-- Trigger function(s) in the Public Schema
-- ----------------------------------------------------------------------------

DROP FUNCTION IF EXISTS update_at() CASCADE;
CREATE FUNCTION update_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = now();
  RETURN new;
END;
$$ LANGUAGE plpgsql;

-- ----------------------------------------------------------------------------
-- Type(s) in the Public Schema
-- ----------------------------------------------------------------------------

DROP TYPE IF EXISTS pg_resp CASCADE;
CREATE TYPE pg_resp AS (
  status     TEXT,
  data       JSONB,
  errors     JSONB,
  error_code TEXT,
  message    TEXT,
  hint       TEXT,
  detail     TEXT
);

DROP TYPE IF EXISTS pg_val CASCADE;
CREATE TYPE pg_val AS (
  passed  BOOLEAN,
  field   TEXT,
  message TEXT
);
