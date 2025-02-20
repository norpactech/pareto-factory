package com.norpactech.pareto.template;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.norpactech.pareto.domain.AttributeVO;
import com.norpactech.pareto.domain.ValidationVO;
import com.norpactech.pareto.service.PgsqlGeneratorService;

@SpringBootTest
class InsertTest {

  @Autowired
  PgsqlGeneratorService pgsqlGeneratorService;

  @Test
  void generateCreate() throws Exception  {

    List<AttributeVO> attributes = List.of(
      new AttributeVO("name", "text"),
      new AttributeVO("description", "text"),
      new AttributeVO("copyright", "text"),
      new AttributeVO("created_by", "text")
    );	 

    List<ValidationVO> validations = List.of(
      new ValidationVO("generic_name", "name")
    );    
    
    boolean hasAudit = true;
    String result = pgsqlGeneratorService.insert("target", "tenant", hasAudit, attributes, validations);
    System.out.println(result);
  }
}