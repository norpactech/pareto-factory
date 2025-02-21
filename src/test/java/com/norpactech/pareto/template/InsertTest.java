package com.norpactech.pareto.template;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.norpactech.pareto.domain.AttributeVO;
import com.norpactech.pareto.domain.FunctionValidationVO;
import com.norpactech.pareto.service.PgsqlGeneratorService;

@SpringBootTest
class InsertTest {

  @Autowired
  PgsqlGeneratorService pgsqlGeneratorService;

  @Test
  void generateCreate() throws Exception  {

    List<AttributeVO> attributes = List.of(
      new AttributeVO("name", "TEXT"),
      new AttributeVO("description", "TEXT"),
      new AttributeVO("copyright", "TEXT") 
    );	 

    List<FunctionValidationVO> validations = List.of(
      new FunctionValidationVO("generic_name", "name")
    );    
    
    boolean hasAudit = true;
    String result = pgsqlGeneratorService.insert("pareto", "tenant", hasAudit, attributes, validations);
    System.out.println(result);
  }
}