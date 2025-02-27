package com.norpactech.pareto.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.norpactech.pareto.entity.User;

@Repository
public class UserRepository extends BaseRepository {

  public UserRepository(JdbcTemplate jdbcTemplate) {
    super(jdbcTemplate);
  }
  /**
   * Find user by username
   */
  public User findByUsername(String username) {
    
    String sql = String.format("select * from %s.user where username = ?", getSchema());

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
    
    String sql = String.format( 
      "INSERT INTO %s.user " +
        "(" +
           "username, " +
           "email, " + 
           "full_name, " + 
           "created_by, " + 
           "updated_by " +
        ") " +
       "VALUES " +
        "(?,?,?,?,?)", getSchema());

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
    
    String sql = String.format( 
      "UPDATE %s.user set " +
        "username = ?, " +
        "email = ?, " +
        "full_name = ?, " +
        "updated_by = ? " +
      "WHERE id = ?", getSchema());

    return jdbcTemplate.update(sql, 
      user.getUsername(), 
      user.getEmail(), 
      user.getFullName(),
      user.getUpdatedBy(),
      user.getId());
  }
  /**
   * Delete
   */
  public int delete(String username) {
    
    String sql = String.format( 
      "DELETE FROM %s.user " +
       "WHERE username = ?", getSchema());

    return jdbcTemplate.update(sql, username); 
  }
}