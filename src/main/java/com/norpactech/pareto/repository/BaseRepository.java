package com.norpactech.pareto.repository;

import java.sql.Connection;
import java.sql.SQLException;

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
    
    try (Connection conn = jdbcTemplate.getDataSource().getConnection()) { 
      this.schema = conn.getSchema();
    } 
    catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return this.schema;
  }
}