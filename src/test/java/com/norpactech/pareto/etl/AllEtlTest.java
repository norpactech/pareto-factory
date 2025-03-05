package com.norpactech.pareto.etl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
class AllEtlTest {

  @Autowired
  JdbcTemplate jdbcTemplate;

  @Autowired
  TenantETL tenantETL;

  @Autowired
  UserETL userETL;

  @Autowired
  RefTableTypeETL refTableTypeETL;

  @Autowired
  RefTablesETL refTablesETL;

  @Autowired
  PropertyTypeETL propertyTypeETL;

  @Autowired
  SchemaETL schemaETL;

  @Autowired
  ObjectETL objectETL;

  @Autowired
  PropertyETL propertyETL;
  
  @Test
  void objectEtlTest() throws Exception  {
	  tenantETL.loadData();
	  userETL.loadData();
    refTableTypeETL.loadData();
    refTablesETL.loadData();
    propertyTypeETL.loadData();
    schemaETL.loadData();
  	objectETL.loadData();
  	propertyETL.loadData();
  }
}