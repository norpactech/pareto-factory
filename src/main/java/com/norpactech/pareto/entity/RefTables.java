package com.norpactech.pareto.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class RefTables extends BaseEntity {
  
  private UUID    idTenant;
  private UUID    idRefTableType;
  private String  name;
  private String  description;
  private String  value;
  private Integer sequence;

  public RefTables() {}
  
  public RefTables(ResultSet rs) throws SQLException {
    super(rs);

    this.idTenant = rs.getObject("id_tenant", UUID.class);
    this.idTenant = rs.getObject("id_ref_table_type", UUID.class);
    this.name = rs.getString("name");
    this.description = rs.getString("description");
    this.value = rs.getString("value");
    this.sequence = rs.getInt("sequence");
  }
}
