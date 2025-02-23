-- -------------------------------------------------------
-- Update ${object}
--
-- Supports Optimistic Locking using Updated At
-- ------------------------------------------------------
CREATE OR REPLACE FUNCTION ${schema}.u_${object}(
  <#list attributes as attribute>
  IN ${attribute.name} ${attribute.type}<#if attribute?has_next>, </#if>
  </#list>
)
RETURNS pg_resp
AS $$
DECLARE

  c_service_name TEXT := 'u_${object}';

  v_metadata     JSONB := '{}'::JSONB;
  v_errors       JSONB := '[]'::JSONB;
  v_val_resp     pg_val;
  v_response     pg_resp;

  v_updates      INT;
  v_message      TEXT;
  v_count        INT;

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
    CALL ${schema}.i_logs(v_response.status, v_response.message, c_service_name, updated_by, v_metadata);
    RETURN v_response;
  END IF;
  </#if>
  
  -- ------------------------------------------------------
  -- Persist
  -- ------------------------------------------------------
  <#if hasAudit>
    <#assign updates = attributes?filter(item -> item.name != "id" && item.name != "updated_at") />
  <#else>
    <#assign updates = attributes?filter(item -> item.name != "id") />
  </#if>

  UPDATE ${schema}.${object} SET
  <#list updates as attribute>
    ${attribute.name} = v_${attribute.name}<#if attribute?has_next || hasAudit>, </#if>
  </#list>
  <#if hasAudit>
    updated_at = CURRENT_TIMESTAMP
  </#if>
  WHERE ${object}.id = v_id
    AND ${object}.updated_at = v_updated_at
  RETURNING ${object}.id, ${object}.updated_at INTO v_id, v_updated_at;

  GET DIAGNOSTICS v_updates = ROW_COUNT;

  IF v_updates > 0 THEN
    -- Record was updated
    v_response := (
      'OK', 
      jsonb_build_object('id', v_id, 'updated_at', v_updated_at), 
      NULL, 
      '00000',
      'Update was successful', 
      NULL, 
      NULL
  );    
  ELSE  
    -- Check for Optimistic Lock Error
    v_id := id;
    SELECT count(*) INTO v_count   
      FROM ${schema}.${object} 
     WHERE ${object}.id = v_id;
          
    IF (v_count > 0) THEN
      -- Record does exists but the updated_at timestamp has changed
      -- The id and updated_at values are not returned. The client must refresh the record.      
      v_message := 'The record was updated by another transaction. Refresh and try again';
      v_response := (
        'ERROR', 
        NULL, 
        jsonb_build_object('type', 'optimistic_lock', 'field', 'updated_at', 'message', v_message), 
        'P0002',
        'No records were found matching the query.',
        'The UPDATED_AT query parameter does not match the current record.',
        'Obtain the latest updated_at timestamp and try again.'          
      );
      CALL ${schema}.i_logs(v_response.status, v_response.message, c_service_name, updated_by, v_metadata);
      RETURN v_response;
    ELSE
      -- Record does not exist
      v_response := (
        'ERROR', 
        NULL, 
        NULL, 
        'P0002',
        'No records were found matching the query.',
        'Check the query parameters or ensure data exists.',
        'The requested resource does not exist in the database.'          
      );
      CALL ${schema}.i_logs(v_response.status, v_response.message, c_service_name, updated_by, v_metadata);
    END IF;
  END IF;    

  RETURN v_response;

  -- ------------------------------------------------------
  -- Exceptions
  -- ------------------------------------------------------
  
  EXCEPTION
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
      CALL ${schema}.i_logs(v_response.status, v_response.message, c_service_name, updated_by, v_metadata);
      RETURN v_response;
  
END;
$$ LANGUAGE plpgsql;
