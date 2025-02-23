-- -------------------------------------------------------
-- Delete ${object}
-- ------------------------------------------------------
CREATE OR REPLACE FUNCTION ${schema}.d_${object}(
  IN id UUID,
  IN updated_at TIMESTAMPTZ,
  IN deleted_by TEXT
)
RETURNS pg_resp
AS $$
DECLARE

  c_service_name TEXT := 'd_${object}';

  v_metadata     JSONB := '{}'::JSONB;
  v_response     pg_resp;
  v_message      TEXT;
  
  v_id           UUID := id;
  v_updated_at   TIMESTAMPTZ := updated_at;
  v_updates      INT;
  v_count        INT;
  
BEGIN

  -- ------------------------------------------------------
  -- Metadata
  -- ------------------------------------------------------

  v_metadata := jsonb_build_object(
    'id', id,
    'updated_at', updated_at,
    'deleted_by', deleted_by
  );
  
  -- ------------------------------------------------------
  -- Delete
  -- ------------------------------------------------------

  DELETE FROM ${schema}.${object} 
   WHERE ${object}.id = v_id
     AND ${object}.updated_at = v_updated_at;

  GET DIAGNOSTICS v_updates = ROW_COUNT;

  IF v_updates > 0 THEN
    -- Record was deleted
    v_response := (
      'OK', 
      NULL, 
      NULL, 
      '00000',
      'Delete was successful', 
      NULL, 
      NULL
    );
    CALL ${schema}.i_logs('INFO', v_response.message, c_service_name, deleted_by, v_metadata);    
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
        '00002',
        'No records were found matching the query.',
        'The UPDATED_AT query parameter does not match the current record.',
        'Obtain the latest updated_at timestamp and try again.'          
      );
      CALL ${schema}.i_logs(v_response.status, v_response.message, c_service_name, deleted_by, v_metadata);
      RETURN v_response;
    ELSE
      -- Record does not exist
      v_response := (
        'ERROR', 
        NULL, 
        NULL, 
        '00002',
        'No records were found matching the query.',
        'Check the query parameters or ensure data exists.',
        'The requested resource does not exist in the database.'          
      );
      CALL ${schema}.i_logs(v_response.status, v_response.message, c_service_name, deleted_by, v_metadata);
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
      CALL ${schema}.i_logs(v_response.status, v_response.message, c_service_name, deleted_by, v_metadata);
      RETURN v_response;
  
END;
$$ LANGUAGE plpgsql;
