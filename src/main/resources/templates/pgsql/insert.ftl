-- -------------------------------------------------------
-- Insert ${object}
-- ------------------------------------------------------
CREATE OR REPLACE FUNCTION ${schema}.i_${object}(
  <#list attributes as attribute>
  IN in_${attribute.name} ${attribute.type}<#if attribute?has_next>, </#if>
  </#list>
)
RETURNS pg_resp
AS $$
DECLARE

  v_val_resp   pg_val;
  v_errors     JSONB := '[]'::JSONB;
  v_response   pg_resp;

  v_id         uuid;
  v_updated_at timestamptz;
  <#if hasAudit>
  v_updated_by text := in_created_by;
  </#if>

BEGIN

  v_metadata := jsonb_build_object(
    'name'       , in_name,
    'description', in_description,
    'is_global'  , in_is_global
  );


  <#if validations?has_content>
  
  -- ------------------------------------------------------
  -- Validations
  -- ------------------------------------------------------
  
  </#if>  
  <#list validations as validation>
  v_val_resp := is_${validation.type}('${validation.attribute}', in_${validation.attribute});
  IF NOT v_val_resp.passed THEN
    v_errors := v_errors || jsonb_build_object('field', v_val_resp.field, 'message', v_val_resp.message);
  END IF;

  </#list>
  <#if validations?has_content>
  IF jsonb_array_length(v_errors) > 0 THEN
    v_response := (
      'error', 
      NULL, 
      v_errors, 
      '23514', 
      'A CHECK constraint was violated due to incorrect input', 
      'Ensure all fields in the ''errors'' array are correctly formatted', 
      'The provided data did not pass validation checks'
    );
    CALL ${schema}.i_logs(response.status, response.message, c_service_name, in_created_by, v_metadata);    
    RETURN v_response;
  END IF;
  </#if>
  
  -- ------------------------------------------------------
  -- Persist
  -- ------------------------------------------------------

  INSERT INTO ${schema}.customer (
  <#list attributes as attribute>
    ${attribute.name}<#if attribute?has_next || hasAudit>, </#if>
  </#list> 
  <#if hasAudit>
    updated_by
  </#if>
  )
  VALUES (
  <#list attributes as attribute>
    in_${attribute.name}<#if attribute?has_next || hasAudit>, </#if>
  </#list>  
  <#if hasAudit>
    v_updated_by
  </#if>
  )
  RETURNING ${object}.id, ${object}.updated_at INTO v_id, v_updated_at;  

  v_response := (
    'success', 
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
        'error', 
        NULL, 
        NULL, 
        '23514', 
        'A UNIQUE constraint was violated due to duplicate data', 
        'A record already exists in the ${object} table', 
        'Check the provided data and try again'
      );
      CALL ${schema}.i_logs(response.status, response.message, c_service_name, in_created_by, v_metadata);
  
    WHEN OTHERS THEN
      v_response := (
        'error', 
        NULL, 
        NULL, 
        SQLSTATE, 
        'An unexpected error occurred', 
        'Check database logs for more details', 
        SQLERRM
      );
      CALL ${schema}.i_logs(response.status, response.message, c_service_name, in_created_by, v_metadata);

    RETURN v_response;
  
END;
$$ LANGUAGE plpgsql;
