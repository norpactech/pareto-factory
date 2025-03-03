package com.norpactech.pareto.etl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ObjectEtlTest {

  @Autowired
  ObjectETL objectETL;

  @Test
  void objectEtlTest() throws Exception  {
    objectETL.loadData();
  }
}