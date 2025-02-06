package com.norpactech.fw.factory.domain;

import lombok.Data;

@Data
public class DatabaseFactoryAboutVO {

  private String factoryName;
  private String version;
  
  public DatabaseFactoryAboutVO(String factoryName, String version) {
    this.factoryName = factoryName;
    this.version = version;
  }
  
}
