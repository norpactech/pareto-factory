package com.norpactech.fw.factory.service;

import org.springframework.stereotype.Service;

import com.norpactech.fw.factory.api.DatabaseFactoryServiceAPI;

@Service("MySqlDatabaseFactoryService")
public class MySqlDatabaseFactoryService  implements DatabaseFactoryServiceAPI {

  public String getFactoryName() {
    return "MySQL";
  }
  
  public String getFactoryVersion() {
    return "0.0.1";
  };  
}