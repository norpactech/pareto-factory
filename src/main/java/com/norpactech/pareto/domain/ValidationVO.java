package com.norpactech.pareto.domain;

import lombok.Data;

@Data
public abstract class ValidationVO {

  String name;
  
  public ValidationVO(String name) {
    this.name = name;
  }
}