-- ----------------------------------------------------------------------------
-- Drop all dependent objects and recreate the schema
-- ----------------------------------------------------------------------------

drop procedure if exists fw.i_logs;
drop procedure if exists fw.i_tenant;
drop type if exists fw.response;

drop table if exists fw.ref_tables;
drop table if exists fw.ref_table_type;
drop table if exists fw.tenant;
drop table if exists fw.logs;

drop function if exists fw.update_at;

drop schema if exists fw;
create schema fw authorization norpac;

-- ----------------------------------------------------------------------------
-- Trigger function to automatically set the created_at field
-- ----------------------------------------------------------------------------

create function fw.update_at()
returns trigger as $$
begin
  new.updated_at = now();
  return new;
end;
$$ language plpgsql;

-- ----------------------------------------------------------------------------
-- Types
-- ----------------------------------------------------------------------------

create type fw.response AS (
  success boolean,
  id uuid,
  updated timestamp,
  message text
);

-- ----------------------------------------------------------------------------
-- Table: logs
-- ----------------------------------------------------------------------------

create table fw.logs (  
  id                    uuid         not null default gen_random_uuid(),
  created_at            timestamptz  not null default current_timestamp,
  level                 varchar(10)  not null check (level in ('DEBUG','INFO','WARNING','ERROR','CRITICAL')),
  message               text         not null,
  service_name          varchar(255) not null,
  created_by            varchar(50),
  metadata JSONB default '{}'::JSONB
);

alter table fw.logs
  add primary key (id);

create index logs_created_at   on fw.logs (created_at desc);
create index logs_level        on fw.logs (level);
create index logs_service_name on fw.logs (service_name);

-- ----------------------------------------------------------------------------
-- Table: tenant
-- ----------------------------------------------------------------------------
create table fw.tenant (
  
  id uuid                                     default gen_random_uuid(),
  name                  varchar(50) not null,
  description           text,
  copyright             varchar(128),
  created_at            timestamptz not null  default current_timestamp,
  created_by            varchar(50) not null,
  updated_at            timestamptz not null  default current_timestamp,
  updated_by            varchar(50) not null,
  is_active             boolean     not null  default true
);

alter table fw.tenant
  add primary key (id);

create unique index tenant_alt_key on fw.tenant(lower(name));

create trigger tenant_update_at
  before update on fw.tenant
    for each row
      execute function fw.update_at();

-- ----------------------------------------------------------------------------
-- Table: ref_table_type
-- ----------------------------------------------------------------------------
create table fw.ref_table_type (
  
  id uuid                                     default gen_random_uuid(),
  name                  varchar(50) not null,
  description           text,
  is_global             boolean     not null,
  created_at            timestamptz not null  default current_timestamp,
  created_by            varchar(50) not null,
  updated_at            timestamptz not null  default current_timestamp,
  updated_by            varchar(50) not null,
  is_active             boolean     not null  default true
);

alter table fw.ref_table_type
  add primary key (id);

create trigger ref_table_type_update_at
  before update on fw.ref_table_type
    for each row
      execute function fw.update_at();

-- ----------------------------------------------------------------------------
-- Table: ref_tables
-- ----------------------------------------------------------------------------
create table fw.ref_tables (
  
  id uuid                                     default gen_random_uuid(),
  id_ref_table_type     uuid        not null,
  name                  varchar(50) not null,
  value                 text        not null,
  sort_seq              int         not null  default 0,
  description           text,
  is_global             boolean     not null,
  created_at            timestamptz not null  default current_timestamp,
  created_by            varchar(50) not null,
  updated_at            timestamptz not null  default current_timestamp,
  updated_by            varchar(50) not null,
  is_active             boolean     not null  default true
);

alter table fw.ref_tables
  add primary key (id);

alter table fw.ref_tables
  add constraint ref_tables_ref_table_type
  foreign key (id_ref_table_type)
  references fw.ref_table_type(id)
  on update cascade
  on delete cascade;
  
create trigger ref_tables_update_at
  before update on fw.ref_tables
    for each row
      execute function fw.update_at();

