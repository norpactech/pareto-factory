package com.norpactech.fw.factory.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MySqlDatabaseFactoryRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;
  
  public String getDatabaseSchema() {

    return jdbcTemplate.queryForObject("select schema();", String.class);
  }
}
