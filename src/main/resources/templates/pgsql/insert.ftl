-- -------------------------------------------------------
-- Insert ${object}
-- ------------------------------------------------------
CREATE OR REPLACE FUNCTION ${schema}.i_${object}(
  <#list attributes as attribute>
  IN ${attribute.name} ${attribute.type}<#if attribute?has_next>, </#if>
  </#list>
)
RETURNS pg_resp
AS $$
DECLARE

  c_service_name TEXT := 'i_${object}';

  v_metadata     JSONB := '{}'::JSONB;
  v_errors       JSONB := '[]'::JSONB;
  v_val_resp     pg_val;
  v_response     pg_resp;

  v_id           UUID;
  v_updated_at   TIMESTAMPTZ;

  -- Set variables to avoid ambiguous column names
  <#list attributes as attribute>
  v_${attribute.name} ${attribute.type} := ${attribute.name};
  </#list>

BEGIN

  -- ------------------------------------------------------
  -- Metadata
  -- ------------------------------------------------------

  v_metadata := jsonb_build_object(
  <#list attributes as attribute>
    '${attribute.name}', ${attribute.name}<#if attribute?has_next>, </#if>
  </#list>
  );
  <#if validations?has_content>
  
  -- ------------------------------------------------------
  -- Validations
  -- ------------------------------------------------------
  
  </#if>  
  <#list validations as validation>
  v_val_resp := is_${validation.name}('${validation.attribute}', ${validation.attribute});
  IF NOT v_val_resp.passed THEN
    v_errors := v_errors || jsonb_build_object('type', 'validation', 'field', v_val_resp.field, 'message', v_val_resp.message);
  END IF;

  </#list>
  <#if validations?has_content>
  IF jsonb_array_length(v_errors) > 0 THEN
    v_response := (
      'ERROR', 
      NULL, 
      v_errors, 
      '23514', 
      'A CHECK constraint was violated due to incorrect input', 
      'Ensure all fields in the ''errors'' array are correctly formatted', 
      'The provided data did not pass validation checks'
    );
    CALL ${schema}.i_logs(v_response.status, v_response.message, c_service_name, created_by, v_metadata);
    RETURN v_response;
  END IF;
  </#if>
  
  -- ------------------------------------------------------
  -- Persist
  -- ------------------------------------------------------
  <#if hasAudit>
    <#assign inserts = attributes?filter(item -> item.name != "created_by") />
  <#else>
    <#assign inserts = attributes />
  </#if>

  INSERT INTO ${schema}.${object} (
  <#list inserts as attribute>
    ${attribute.name}<#if attribute?has_next || hasAudit>, </#if>
  </#list> 
  <#if hasAudit>
    created_by,
    updated_by
  </#if>
  )
  VALUES (
  <#list inserts as attribute>
    v_${attribute.name}<#if attribute?has_next || hasAudit>, </#if>
  </#list>  
  <#if hasAudit>
    v_created_by,
    v_created_by
  </#if>
  )
  RETURNING ${object}.id, ${object}.updated_at INTO v_id, v_updated_at;  

  v_response := (
    'OK', 
    jsonb_build_object('id', v_id, 'updated_at', v_updated_at), 
    NULL, NULL, NULL, NULL, NULL
  );
  RETURN v_response;

  -- ------------------------------------------------------
  -- Exceptions
  -- ------------------------------------------------------
  
  EXCEPTION
    WHEN UNIQUE_VIOLATION THEN
      v_response := (
        'ERROR', 
        NULL, 
        NULL, 
        '23514', 
        'A UNIQUE constraint was violated due to duplicate data', 
        'A record already exists in the ${object} table', 
        'Check the provided data and try again'
      );
      CALL ${schema}.i_logs(v_response.status, v_response.message, c_service_name, created_by, v_metadata);
      RETURN v_response;
  
    WHEN OTHERS THEN
      v_response := (
        'ERROR', 
        NULL, 
        NULL, 
        SQLSTATE, 
        'An unexpected error occurred', 
        'Check database logs for more details', 
        SQLERRM
      );
      CALL ${schema}.i_logs(v_response.status, v_response.message, c_service_name, created_by, v_metadata);
      RETURN v_response;
  
END;
$$ LANGUAGE plpgsql;
