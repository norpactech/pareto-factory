package com.norpactech.pareto.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.norpactech.pareto.entity.User;

@Repository
public class UserRepository {

  @Autowired
  JdbcTemplate jdbcTemplate;
  
  /**
   * Find user by username
   */
  public User findByUsername(String username) {
    
    String sql = "select * from pareto.user where username = ?";
    
    try {
      return jdbcTemplate.queryForObject(sql, new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
          return new User(rs);
        }
      }, username);
    }
    catch(EmptyResultDataAccessException e) {
      return null;
    }
  }
  /**
   * Insert
   */
  public int insert(User user) {
    
    String sql = "INSERT INTO pareto.user " +
        "(" +
           "username, " +
           "email, " + 
           "full_name, " + 
           "created_by, " + 
           "updated_by " +
        ") " +
      "VALUES " +
        "(?,?,?,?,?)";

    return jdbcTemplate.update(sql, 
      user.getUsername(), 
      user.getEmail(), 
      user.getFullName(),
      user.getCreatedBy(),
      user.getCreatedBy());
  }
  /**
   * Update
   */
  public int update(User user) {
    
    String sql = "UPDATE pareto.user set " +
                   "username = ?, " +
                   "email = ?, " +
                   "full_name = ?, " +
                   "updated_by = ? " +
                  "WHERE id = ?";

    return jdbcTemplate.update(sql, 
      user.getUsername(), 
      user.getEmail(), 
      user.getFullName(),
      user.getUpdatedBy(),
      user.getId());
  }
}