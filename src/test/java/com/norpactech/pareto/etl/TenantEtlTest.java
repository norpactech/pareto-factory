package com.norpactech.pareto.etl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TenantEtlTest {

  @Autowired
  TenantETL tenantETL;

  @Test
  void tenantEtlTest() throws Exception  {
    tenantETL.loadData();
  }
}