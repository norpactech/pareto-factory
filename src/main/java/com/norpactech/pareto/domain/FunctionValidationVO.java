package com.norpactech.pareto.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class FunctionValidationVO extends ValidationVO {

  String attribute;
  
  public FunctionValidationVO(String name, String attribute) {
    super(name);
    this.attribute = attribute;
  }
}