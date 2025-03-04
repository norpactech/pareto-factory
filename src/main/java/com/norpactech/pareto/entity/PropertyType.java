package com.norpactech.pareto.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class PropertyType extends BaseEntity {
  
  private UUID    idTenant;
  private UUID    idRtDataType;
  private String  name;
  private String  description;
  private Integer length;
  private Integer precision;
  private Boolean isNullable;
  private String  defaultValue;
  private String  validation;

  public PropertyType() {}
  
  public PropertyType(ResultSet rs) throws SQLException {
    super(rs);

    this.idTenant = rs.getObject("id_tenant", UUID.class);
    this.idRtDataType = rs.getObject("id_rt_data_type", UUID.class);
    this.name = rs.getString("name");
    this.description = rs.getString("description");
    this.length = valueOf(rs, "length", Integer.class);    
    this.precision = valueOf(rs, "precision", Integer.class);
    this.isNullable = valueOf(rs, "is_nullable", Boolean.class);
    this.defaultValue = rs.getString("default_value");
    this.validation = rs.getString("validation");
  }
}
