package com.norpactech.pareto.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class Tenant extends BaseEntity {
  
  private String name;
  private String description;
  private String copyright;

  public Tenant() {}
  
  public Tenant(ResultSet rs) throws SQLException {
    super(rs.getObject("id"), 
          rs.getTimestamp("created_at"), 
          rs.getString("created_by"), 
          rs.getTimestamp("updated_at"),
          rs.getString("updated_by"), 
          rs.getBoolean("is_active"));

    this.name = rs.getString("name");
    this.description = rs.getString("description");
    this.copyright = rs.getString("copyright");
  }
}
