package com.norpactech.pareto.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import lombok.Data;

@Data
public abstract class BaseEntity {

  private UUID id;
  private String createdBy;
  private Timestamp createdAt;
  private String updatedBy;
  private Timestamp updatedAt;
  private Boolean isActive;

  public BaseEntity() {}
  
  public BaseEntity(ResultSet rs) throws SQLException {

    this.id = rs.getObject("id", UUID.class); 
    this.createdAt = rs.getTimestamp("created_at"); 
    this.createdBy = rs.getString("created_by"); 
    this.updatedAt = rs.getTimestamp("updated_at");
    this.updatedBy = rs.getString("updated_by"); 
    this.isActive = rs.getBoolean("is_active");
  }
}
