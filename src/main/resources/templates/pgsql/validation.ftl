-- ----------------------------------------------------------------------------
-- © 2025 Northern Pacific Technologies, LLC.
-- Licensed under the MIT License.
-- See LICENSE file in the project root for full license information.
-- ----------------------------------------------------------------------------

<#list validations as validation>
-- --------------------------------------------------------
-- Validate ${validation.name}
-- --------------------------------------------------------
CREATE OR REPLACE FUNCTION is_${validation.name}(
  IN in_attribute TEXT,
  IN in_value     ${validation.dataType}
) 
RETURNS pg_val
AS $$
DECLARE

  v_result pg_val;

BEGIN
    
  IF in_value ~* ${validation.regex} THEN
    v_result := (TRUE, in_attribute, NULL);
  ELSE
    v_result := (FALSE, in_attribute, 'Invalid ${validation.name} format');
  END IF;

  RETURN v_result;

END;
$$ LANGUAGE plpgsql;
</#list>


