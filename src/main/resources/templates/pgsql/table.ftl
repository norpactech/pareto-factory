DROP TABLE IF EXISTS ${schema}.{object} CASCADE;

CREATE TABLE ${schema}.{object} (
    <#list schema.properties as property>
    ${property.name} ${property.type}<#if property.notNull> NOT NULL</#if><#if property.default> NOT NULL</#if><#if property?has_next>,</#if>
    </#list>
);




-- ------------------------------------------------------------------
-- ©2024 $(COPYRIGHT_AUTHOR). All Rights Reserved.
-- 
-- DDL and Functions for: $(SCHEMA).$(TABLE_NAME)
-- ------------------------------------------------------------------
drop table if exists $(SCHEMA).$(TABLE_NAME) cascade;

create table $(SCHEMA).$(TABLE_NAME) (
$(COLUMNS)
);

