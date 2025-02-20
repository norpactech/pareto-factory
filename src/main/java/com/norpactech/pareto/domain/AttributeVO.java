package com.norpactech.pareto.domain;

import lombok.Data;

@Data
public class AttributeVO {

  String name;
  String type;
  
  public AttributeVO(String name, String type) {
    this.name = name;
    this.type = type;
  }
}
