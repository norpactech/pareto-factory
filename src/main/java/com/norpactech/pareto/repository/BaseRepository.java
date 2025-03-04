package com.norpactech.pareto.repository;

import org.springframework.jdbc.core.JdbcTemplate;

public abstract class BaseRepository {
  
  protected final JdbcTemplate jdbcTemplate;

  public BaseRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }
  
  String schema = null;
  
  public String getSchema() {

    if (this.schema != null) {
      return this.schema;
    }
    
    try {
      this.schema = jdbcTemplate.getDataSource().getConnection().getSchema();
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
    return this.schema;
  }
}