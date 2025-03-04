package com.norpactech.pareto.repository;

import java.util.UUID;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.norpactech.pareto.entity.RefTableType;

@Repository
public class RefTableTypeRepository extends BaseRepository {

  public RefTableTypeRepository(JdbcTemplate jdbcTemplate) {
    super(jdbcTemplate);
  }
  /**
   * Find by alternate key
   * @throws Exception 
   */
  public RefTableType findByAltKey(UUID idTenant, String name) throws Exception {
    
    String sql = String.format("select * from %s.ref_table_type where id_tenant = ? and name = ?", getSchema());

    try {
      return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new RefTableType(rs), idTenant, name);
    } 
    catch (EmptyResultDataAccessException e) {
      return null;
    }    
  }
  /**
   * Insert
   */
  public int insert(RefTableType refTableType) {
    
    String sql = String.format( 
      "INSERT INTO %s.ref_table_type " +
        "(" +
           "id_tenant, " + 
           "name, " +
           "description, " + 
           "created_by, " + 
           "updated_by " +
        ") " +
       "VALUES " +
        "(?,?,?,?,?)", getSchema());

    return jdbcTemplate.update(sql, 
      refTableType.getIdTenant(),
      refTableType.getName(), 
      refTableType.getDescription(), 
      refTableType.getCreatedBy(),
      refTableType.getCreatedBy());
  }
  /**
   * Update
   */
  public int update(RefTableType refTableType) {
    
    String sql = String.format( 
      "UPDATE %s.ref_table_type set " +
        "id_tenant = ?, " +
        "name = ?, " +
        "description = ?, " +
        "updated_by = ? " +
      "WHERE id = ?", getSchema());

    return jdbcTemplate.update(sql, 
      refTableType.getIdTenant(),
      refTableType.getName(), 
      refTableType.getDescription(), 
      refTableType.getUpdatedBy(),
      refTableType.getId());
  }
  /**
   * Delete
   */
  public int delete(UUID idTenant, String name) throws Exception {
    
    String sql = String.format( 
      "DELETE FROM %s.ref_table_type " +
       "WHERE id_tenant = ? " +
         "AND name = ?", getSchema());

    return jdbcTemplate.update(sql, idTenant, name); 
  }
}