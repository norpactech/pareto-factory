package com.norpactech.pareto.repository;

import java.util.UUID;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ObjectRepository extends BaseRepository {

  public ObjectRepository(JdbcTemplate jdbcTemplate) {
    super(jdbcTemplate);
  }
  /**
   * Find by alternate key
   * @throws Exception 
   */
  public com.norpactech.pareto.entity.Object findByAltKey(UUID idSchema, String name) throws Exception {
    
    String sql = String.format("select * from %s.object where id_schema = ? and lower(name) = lower(?)", getSchema());

    try {
      return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new com.norpactech.pareto.entity.Object(rs), idSchema, name);
    } 
    catch (EmptyResultDataAccessException e) {
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
        "description = ?, " +
        "has_identifier = ?, " +
        "has_audit = ?, " +
        "updated_by = ? " +
      "WHERE id = ?", getSchema());

    return jdbcTemplate.update(sql, 
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
         "AND lower(name) = lower(?)", getSchema());

    return jdbcTemplate.update(sql, idSchema, name); 
  }
}