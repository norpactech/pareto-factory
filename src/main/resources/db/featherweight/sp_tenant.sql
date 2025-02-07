
drop procedure if exists fw.i_tenant;
create procedure fw.i_tenant(
  in in_name        varchar,
  in in_description text,
  in in_copyright   varchar,
  in in_created_by  varchar,
  out response      fw.response
)
language plpgsql
as $$
declare
 
  v_id uuid;
  v_updated_at timestamp;
  
begin

  insert into fw.tenant (
    name,
    description,
    copyright,
    created_by,
	updated_by
  )
  values (
    in_name,
    in_description,
    in_copyright,
    in_created_by,
	in_created_by
  )
  returning id, updated_at into v_id, v_updated_at;
  response.success := true;
  response.id := v_id;
  response.updated := v_updated_at;
  response.message := 'Insert successful';
  
exception
  when unique_violation then
    response.success := false;
    response.id := null;
    response.updated := null;
    response.message := 'Error: Tenant already exists.';
  when others then
    response.success := false;
    response.id := null;
    response.updated := null;
    response.message := 'An unexpected error occurred: ' || sqlerrm;
end;
$$;



