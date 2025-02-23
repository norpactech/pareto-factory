package com.norpactech.pareto.template;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.norpactech.pareto.service.PgsqlGeneratorService;

@SpringBootTest
class DeleteTest {

  @Autowired
  PgsqlGeneratorService pgsqlGeneratorService;

  @Test
  void generateCreate() throws Exception  {

    String result = pgsqlGeneratorService.delete("pareto", "tenant");
    
    Path path = Path.of("/Users/scott/pareto/pareto-db/Scripts/functions/d_tenant.sql");
    Files.write(path, result.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    
    System.out.println(result);
  }
}


