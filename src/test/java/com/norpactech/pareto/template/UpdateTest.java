package com.norpactech.pareto.template;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.norpactech.pareto.domain.AttributeVO;
import com.norpactech.pareto.domain.FunctionValidationVO;
import com.norpactech.pareto.service.PgsqlGeneratorService;

@SpringBootTest
class UpdateTest {

  @Autowired
  PgsqlGeneratorService pgsqlGeneratorService;

  @Test
  void generateCreate() throws Exception  {

    List<AttributeVO> attributes = List.of(
      new AttributeVO("id", "UUID"),
      new AttributeVO("name", "TEXT"),
      new AttributeVO("description", "TEXT"),
      new AttributeVO("copyright", "TEXT"), 
      new AttributeVO("updated_at", "TIMESTAMPTZ"), 
      new AttributeVO("updated_by", "TEXT")
    );	 

    List<FunctionValidationVO> validations = List.of(
      new FunctionValidationVO("generic_name", "name")
    );    
    
    boolean hasAudit = true;
    String result = pgsqlGeneratorService.update("pareto", "tenant", hasAudit, attributes, validations);
    
    Path path = Path.of("/Users/scott/pareto/pareto-db/Scripts/functions/u_tenant.sql");
    Files.write(path, result.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    
    System.out.println(result);
  }
}


