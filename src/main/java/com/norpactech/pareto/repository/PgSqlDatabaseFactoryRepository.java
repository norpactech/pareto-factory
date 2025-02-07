package com.norpactech.pareto.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PgSqlDatabaseFactoryRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;
  
  public String getDatabaseSchema() {

    return jdbcTemplate.queryForObject("select current_schema();", String.class);
  }  
}
