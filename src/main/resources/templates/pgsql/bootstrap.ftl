-- ----------------------------------------------------------------------------
-- © 2025 Northern Pacific Technologies, LLC.
-- Licensed under the MIT License.
-- See LICENSE file in the project root for full license information.
-- ----------------------------------------------------------------------------

DROP schema IF EXISTS ${schema} cascade;
CREATE schema ${schema} AUTHORIZATION ${owner};

-- ----------------------------------------------------------------------------
-- Trigger function(s) in the Public Schema
-- ----------------------------------------------------------------------------

DROP FUNCTION IF EXISTS update_at() CASCADE;
CREATE FUNCTION update_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
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

-- ----------------------------------------------------------------------------
-- Logging
-- ----------------------------------------------------------------------------

CREATE TABLE ${schema}.logs (  
  id           UUID                   DEFAULT gen_random_uuid(),
  created_at   TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by   TEXT          NOT NULL DEFAULT 'unavailable',
  level        TEXT          NOT NULL CHECK (level IN ('DEBUG','INFO','WARNING','ERROR','CRITICAL')),
  message      TEXT          NOT NULL,
  service_name TEXT          NOT NULL,
  metadata JSONB default '{}'::JSONB
);

ALTER TABLE ${schema}.logs
  ADD PRIMARY KEY (id);

CREATE INDEX logs_created_at   ON ${schema}.logs (created_at DESC);
CREATE INDEX logs_level        ON ${schema}.logs (level);
CREATE INDEX logs_service_name ON ${schema}.logs (service_name);

-- Insert Logs

DROP PROCEDURE IF EXISTS ${schema}.i_logs;
CREATE PROCEDURE ${schema}.i_logs(
  in in_level         text,
  in in_message       text,
  in in_service_name  text,
  in in_created_by    text,
  in in_metadata      jsonb
)
LANGUAGE plpgsql
AS $$
DECLARE
begin

  INSERT INTO ${schema}.logs (
    level,
    message,
    service_name,
    created_by,
    metadata
  )
  VALUES (
    in_level,
    in_message,
    in_service_name,
    in_created_by,
    in_metadata
  );

END;
$$;