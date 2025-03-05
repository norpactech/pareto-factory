package com.norpactech.pareto.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class Property extends BaseEntity {
  
  private UUID    idObject;
  private Integer sequence;
  private String  name;
  private String  description;
  private String  dataType;
  private Integer length;
  private Integer precision;
  private Boolean isNullable;
  private String  defaultValue;
  private String  validation;

  public Property() {}
  
  public Property(ResultSet rs) throws SQLException {
    super(rs);

    this.idObject = rs.getObject("id_object", UUID.class);
    this.sequence = rs.getInt("sequence");
    this.name = rs.getString("name");
    this.description = rs.getString("description");
    this.dataType = rs.getString("data_type");
    this.length = valueOf(rs, "length", Integer.class);    
    this.precision = valueOf(rs, "precision", Integer.class);
    this.isNullable = valueOf(rs, "is_nullable", Boolean.class);
    this.defaultValue = rs.getString("default_value");
    this.validation = rs.getString("validation");
  }
}
