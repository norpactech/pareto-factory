
drop procedure if exists fw.i_tenant;
drop procedure if exists fw.u_tenant;
drop procedure if exists fw.d_tenant_by_alt_key;

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
 
  c_service_name constant varchar := 'fw.i_tenant';
  v_id uuid;
  v_updated_at timestamp;
  v_metadata   jsonb;
  
begin

  v_metadata := jsonb_build_object(
    'name'       , in_name,
    'description', in_description,
    'copyright'  , in_copyright
  );

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
    call fw.i_logs('ERROR', response.message, c_service_name, in_created_by, v_metadata);
	
  when others then
    response.success := false;
    response.id := null;
    response.updated := null;
    response.message := 'An unexpected error occurred: ' || sqlerrm;
    call fw.i_logs('ERROR', response.message, c_service_name, in_created_by, v_metadata);
	
end;
$$;

-- ----------------------------------------------------------------------------
-- Update
-- ----------------------------------------------------------------------------

create procedure fw.u_tenant(
  in in_id          uuid,
  in in_name        varchar,
  in in_description text,
  in in_copyright   varchar,
  in in_updated_by  varchar,
  out response      fw.response
)
language plpgsql
as $$
declare
 
  c_service_name constant varchar := 'fw.u_tenant';
  v_updated_at timestamp;
  v_metadata   jsonb;
  
begin

  v_metadata := jsonb_build_object(
    'name'       , in_name,
    'description', in_description,
    'copyright'  , in_copyright
  );

  update fw.tenant set 
    name = in_name,
    description = in_description,
    copyright = in_copyright,
	updated_by = in_updated_by
  where id = in_id
  returning updated_at into v_updated_at;

  if not found then
    response.success := false;
    response.id := null;
    response.updated := null;
    response.message := 'Error: Tenant does not exist for primary key: ' || in_uuid;
    call fw.i_logs('ERROR', response.message, c_service_name, in_updated_by, v_metadata);
  else
    response.success := true;
    response.id := in_id;
    response.updated := v_updated_at;
    response.message := 'Update successful';
  end if;

exception
  when others then
    response.success := false;
    response.id := null;
    response.updated := null;
    response.message := 'An unexpected error occurred: ' || sqlerrm;
    call fw.i_logs('ERROR', response.message, c_service_name, in_updated_by, v_metadata);
	
end;
$$;

-- ----------------------------------------------------------------------------
-- Delete by Alt Key
-- ----------------------------------------------------------------------------

create procedure fw.d_tenant_by_alt_key(
  in in_name        varchar,
  in in_deleted_by  varchar,
  out response      fw.response
)
language plpgsql
as $$
declare
 
  c_service_name constant varchar := 'fw.d_tenant';
  v_metadata   jsonb;
  
begin

  v_metadata := jsonb_build_object(
    'name', in_name
  );

  delete from fw.tenant 
   where name = in_name;

  if not found then
    response.success := false;
    response.id := null;
    response.updated := null;
    response.message := 'Error: Tenant does not exist for alternate key: ' || in_name;
    call fw.i_logs('ERROR', response.message, c_service_name, in_deleted_by, v_metadata);
  else
    response.success := true;
    response.id := null;
    response.updated := null;
    response.message := 'Delete successful';
    call fw.i_logs('INFO', response.message, c_service_name, in_deleted_by, v_metadata);
  end if;

exception
  when others then
    response.success := false;
    response.id := null;
    response.updated := null;
    response.message := 'An unexpected error occurred: ' || sqlerrm;
    call fw.i_logs('ERROR', response.message, c_service_name, in_deleted_by, v_metadata);
	
end;
$$;


