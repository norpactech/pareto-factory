package com.norpactech.pareto.domain;

import lombok.Data;

@Data
public class DatabaseFactoryAboutVO {

  private String factoryName;
  private String version;
  private String databaseSchema;
  
  public DatabaseFactoryAboutVO(
    String factoryName, 
    String version, 
    String databaseSchema) {
    
    this.factoryName = factoryName;
    this.version = version;
    this.databaseSchema = databaseSchema;
  }
}
