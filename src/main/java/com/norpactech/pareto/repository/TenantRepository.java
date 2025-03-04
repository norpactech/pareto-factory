package com.norpactech.pareto.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.norpactech.pareto.entity.Tenant;

@Repository
public class TenantRepository extends BaseRepository {

  public TenantRepository(JdbcTemplate jdbcTemplate) {
    super(jdbcTemplate);
  }
  /**
   * Find by alternate key
   */
  public Tenant findByAltKey(String name) {
    
    String sql = String.format("select * from %s.tenant where name = ?", getSchema());

    try {
      return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Tenant(rs), name);
    } 
    catch (EmptyResultDataAccessException e) {
      return null;
    }    
  }
  /**
   * Insert
   */
  public int insert(Tenant tenant) {
    
    String sql = String.format( 
      "INSERT INTO %s.tenant " +
        "(" +
           "name, " +
           "description, " + 
           "copyright, " + 
           "created_by, " + 
           "updated_by " +
        ") " +
       "VALUES " +
        "(?,?,?,?,?)", getSchema());

    return jdbcTemplate.update(sql, 
      tenant.getName(), 
      tenant.getDescription(), 
      tenant.getCopyright(),
      tenant.getCreatedBy(),
      tenant.getCreatedBy());
  }
  /**
   * Update
   */
  public int update(Tenant tenant) {
    
    String sql = String.format( 
      "UPDATE %s.tenant set " +
        "name = ?, " +
        "description = ?, " +
        "copyright = ?, " +
        "updated_by = ? " +
      "WHERE id = ?", getSchema());

    return jdbcTemplate.update(sql, 
      tenant.getName(), 
      tenant.getDescription(), 
      tenant.getCopyright(),
      tenant.getUpdatedBy(),
      tenant.getId());
  }
  /**
   * Delete
   */
  public int delete(String name) {
    
    String sql = String.format( 
      "DELETE FROM %s.tenant " +
       "WHERE name = ?", getSchema());

    return jdbcTemplate.update(sql, name); 
  }
}