package com.norpactech.pareto.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class User extends BaseEntity {
  
  private String username;
  private String email;
  private String fullName;

  public User() {}
  
  public User(ResultSet rs) throws SQLException {
    super(rs);

    this.username = rs.getString("username");
    this.email = rs.getString("email");
    this.fullName = rs.getString("full_name");
  }
}
