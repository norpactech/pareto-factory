package com.norpactech.pareto.domain;

import lombok.Data;

@Data
public class ValidationVO {

  String type;
  String attribute;
  
  public ValidationVO(String type, String attribute) {
    this.type = type;
    this.attribute = attribute;
  }
}
