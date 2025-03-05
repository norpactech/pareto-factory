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
    this.isActive = valueOf(rs, "is_active", Boolean.class);
  }
  /**
   * Gets the 'real' value. For example, getInt() returns 0 when the 
   * actual value is null. This method will return null if the actual
   * value is null.
   * 
   * @param <T> Class type
   * @param rs ResultSet
   * @param property Column name
   * @param type Class type
   * @return Real Value (data value or null)
   * @throws SQLException
   */
  @SuppressWarnings("unchecked")
  protected <T> T valueOf(ResultSet rs, String property, Class<T> type) throws SQLException {

    if (type == Integer.class) {
      Integer value = rs.getInt(property);
      return (T) (rs.wasNull() ? null : value);
    }
    if (type == Long.class) {
      Long value = rs.getLong(property);
      return (T) (rs.wasNull() ? null : value);
    }
    if (type == Float.class) {
      Float value = rs.getFloat(property);
      return (T) (rs.wasNull() ? null : value);
    }
    if (type == Double.class) {
      Double value = rs.getDouble(property);
      return (T) (rs.wasNull() ? null : value);
    }
    else if (type == Boolean.class) {
      Boolean value = rs.getBoolean(property);
      return (T) (rs.wasNull() ? null : value);
    }
    else {
      throw new RuntimeException("Type not supported: " + type.getName());
    }
  } 
}
