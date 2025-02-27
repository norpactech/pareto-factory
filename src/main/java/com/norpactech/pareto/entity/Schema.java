package com.norpactech.pareto.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class Schema extends BaseEntity {
  
  private UUID   idTenant;
  private String name;
  private String description;

  public Schema() {}
  
  public Schema(ResultSet rs) throws SQLException {
    super(rs);

    this.idTenant = rs.getObject("id_tenant", UUID.class);
    this.name = rs.getString("name");
    this.description = rs.getString("description");
  }
}
