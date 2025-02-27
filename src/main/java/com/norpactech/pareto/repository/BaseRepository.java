package com.norpactech.pareto.repository;

import org.springframework.jdbc.core.JdbcTemplate;

public abstract class BaseRepository {
  
  protected final JdbcTemplate jdbcTemplate;

  public BaseRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }
  
  public String getSchema() {
    
    String schema = null;
    
    try {
      schema = jdbcTemplate.getDataSource().getConnection().getSchema();
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
    return schema;
  }
}