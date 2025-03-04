package com.norpactech.pareto.etl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RefTableTypeEtlTest {

  @Autowired
  RefTableTypeETL refTableTypeETL;

  @Test
  void schemaEtlTest() throws Exception  {
    refTableTypeETL.loadData();
  }
}