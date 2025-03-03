package com.norpactech.pareto.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class Object extends BaseEntity {
  
  private UUID   idSchema;
  private String name;
  private String description;
  private Boolean hasIdentifier;
  private Boolean hasAudit;

  public Object() {}
  
  public Object(ResultSet rs) throws SQLException {
    super(rs);

    this.idSchema = rs.getObject("id_schema", UUID.class);
    this.name = rs.getString("name");
    this.description = rs.getString("description");
    this.hasIdentifier = rs.getBoolean("has_identifier");
    this.hasAudit = rs.getBoolean("has_audit");
  }
}	
