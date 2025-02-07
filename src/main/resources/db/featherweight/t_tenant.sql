
drop procedure if exists fw.t_tenant;
create procedure fw.t_tenant()
language plpgsql
as $$
declare
 
  c_test_name   constant varchar := 'fw.t_tenant';
  c_name        constant varchar := 't_tenant';
  c_description constant varchar := 't_description';
  c_copyright   constant varchar := 't_copyright';
  c_username    constant varchar := 't_username';
  v_response    fw.response;
  
begin

  call fw.i_tenant(c_name, c_description, c_copyright, 'Scott1', v_response);
  raise notice '%, %, %, %', v_response.success, v_response.id, v_response.updated, v_response.message;

  call fw.u_tenant(v_response.id, c_name, c_description, c_copyright, 'Scott2', v_response);
  raise notice '%, %, %, %', v_response.success, v_response.id, v_response.updated, v_response.message;

  call fw.d_tenant_by_alt_key(c_name, c_username, v_response);
  raise notice '%, %, %, %', v_response.success, v_response.id, v_response.updated, v_response.message;
	
end;
$$;

call fw.t_tenant();
drop procedure if exists fw.t_tenant;
