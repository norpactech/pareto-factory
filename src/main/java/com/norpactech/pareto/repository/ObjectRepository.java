package com.norpactech.pareto.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ObjectRepository extends BaseRepository {

  @Autowired
  TenantRepository tenantRepository;
  
  public ObjectRepository(JdbcTemplate jdbcTemplate) {
    super(jdbcTemplate);
  }
  /**
   * Find by alternate key
   * @throws Exception 
   */
  public com.norpactech.pareto.entity.Object findByAltKey(UUID idSchema, String name) throws Exception {
    
    String sql = String.format("select * from %s.object where id_schema = ? and name = ?", getSchema());

    try {
      return jdbcTemplate.queryForObject(sql, new RowMapper<com.norpactech.pareto.entity.Object>() {
        @Override
        public com.norpactech.pareto.entity.Object mapRow(ResultSet rs, int rowNum) throws SQLException {
          return new com.norpactech.pareto.entity.Object(rs);
        }
      }, idSchema, name);
    }
    catch(EmptyResultDataAccessException e) {
      return null;
    }
  }
  /**
   * Insert
   */
  public int insert(com.norpactech.pareto.entity.Object object) {
    
    String sql = String.format( 
      "INSERT INTO %s.object " +
        "(" +
           "id_schema, " + 
           "name, " +
           "description, " +
           "has_identifier, " +
           "has_audit, " +
           "created_by, " + 
           "updated_by " +
        ") " +
       "VALUES " +
        "(?,?,?,?,?,?,?)", getSchema());

    return jdbcTemplate.update(sql, 
      object.getIdSchema(),
      object.getName(), 
      object.getDescription(), 
      object.getHasIdentifier(), 
      object.getHasAudit(), 
      object.getCreatedBy(),
      object.getCreatedBy());
  }
  /**
   * Update
   */
  public int update(com.norpactech.pareto.entity.Object object) {
    
    String sql = String.format( 
      "UPDATE %s.object set " +
        "id_schema = ?, " +
        "name = ?, " +
        "description = ?, " +
        "has_identifier = ?, " +
        "has_audit = ?, " +
        "updated_by = ? " +
      "WHERE id = ?", getSchema());

    return jdbcTemplate.update(sql, 
      object.getIdSchema(),
      object.getName(), 
      object.getDescription(), 
      object.getHasIdentifier(), 
      object.getHasAudit(), 
      object.getUpdatedBy(),
      object.getId());
  }
  /**
   * Delete
   */
  public int delete(UUID idSchema, String name) throws Exception {
    
    String sql = String.format( 
      "DELETE FROM %s.object " +
       "WHERE id_schema = ? " +
         "AND name = ?", getSchema());

    return jdbcTemplate.update(sql, idSchema, name); 
  }
}