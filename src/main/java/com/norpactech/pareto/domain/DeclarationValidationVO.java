package com.norpactech.pareto.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class DeclarationValidationVO extends ValidationVO {

  String dataType;
  String regex;
  
  public DeclarationValidationVO(String name, String dataType, String regex) {
    super(name);
    this.dataType = dataType;
    this.regex = regex;
  }
}