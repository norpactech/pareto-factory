package com.norpactech.pareto.etl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserEtlTest {

  @Autowired
  UserETL userETL;
  
  @Test
  void userEtlTest() throws Exception  {
    userETL.loadData();
  }
}
