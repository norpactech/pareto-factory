package com.norpactech.pareto.template;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.norpactech.pareto.domain.DeclarationValidationVO;
import com.norpactech.pareto.service.PgsqlGeneratorService;

@SpringBootTest
class ValidationTest {

  @Autowired
  PgsqlGeneratorService pgsqlGeneratorService;

  @Test
  void generateCreate() throws Exception  {

    List<DeclarationValidationVO> validations = List.of(
      new DeclarationValidationVO("generic_name", "TEXT", "'^[A-Za-z]+(['' -][A-Za-z]+)*$'")
    );        
    
    String result = pgsqlGeneratorService.validations(validations);
    System.out.println(result);
  }
}