package com.norpactech.pareto.etl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PropertyTypeEtlTest {

  @Autowired
  PropertyTypeETL propertyTypeETL;

  @Test
  void propertyTypeEtlTest() throws Exception  {
    propertyTypeETL.loadData();
  }
}