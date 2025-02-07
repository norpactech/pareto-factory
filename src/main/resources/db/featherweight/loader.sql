
drop procedure if exists fw.loader;
create procedure fw.loader()
language plpgsql
as $$
declare 

  response fw.response;

begin

  call fw.i_tenant('norpac-plsql', 'Featherweight Test Postgres System', 'Norther Pacific Technologies', 'Scott', response);
  raise notice '%, %, %, %', response.success, response.id, response.updated, response.message;
  
  call fw.i_tenant('norpac-mysql', 'Featherweight Test MySQL System', 'Norther Pacific Technologies', 'Scott', response);
  raise notice '%, %, %, %', response.success, response.id, response.updated, response.message;

  

end;
$$;

call fw.loader();
drop procedure if exists fw.loader;