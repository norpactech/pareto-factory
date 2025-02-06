package com.norpactech.fw.factory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.norpactech.fw.factory.api.DatabaseFactoryServiceAPI;
import com.norpactech.fw.factory.repository.PgSqlDatabaseFactoryRepository;

@Service("PgSqlDatabaseFactoryService")
public class PgSqlDatabaseFactoryService implements DatabaseFactoryServiceAPI {

  @Autowired
  PgSqlDatabaseFactoryRepository repository;

  public String getFactoryName() {
    return "PostgreSQL";
  }
  
  public String getFactoryVersion() {
    return "0.0.1";
  };
  
  public String getDatabaseSchema() {
    return repository.getDatabaseSchema();
  }
}
