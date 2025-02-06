package com.norpactech.fw.factory.service;

import org.springframework.stereotype.Service;

import com.norpactech.fw.factory.api.DatabaseFactoryServiceAPI;

@Service("PgSqlDatabaseFactoryService")
public class PgSqlDatabaseFactoryService implements DatabaseFactoryServiceAPI {

  public String getFactoryName() {
    return "PostgreSQL";
  }
  
  public String getFactoryVersion() {
    return "0.0.1";
  };    
}
