package com.norpactech.pareto.template;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.norpactech.pareto.service.PgsqlGeneratorService;

@SpringBootTest
class BootstrapTest {

  @Autowired
  PgsqlGeneratorService pgsqlGeneratorService;
  
	@Test
	void generateCreate() throws Exception  {

    String result = pgsqlGeneratorService.bootstrap("norpac", "system");

    System.out.println(result);
	}
}
