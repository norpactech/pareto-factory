package com.norpactech.pareto.repository;

import java.util.UUID;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.norpactech.pareto.entity.RefTables;

@Repository
public class RefTablesRepository extends BaseRepository {

  public RefTablesRepository(JdbcTemplate jdbcTemplate) {
    super(jdbcTemplate);
  }
  /**
   * Find by alternate key
   * @throws Exception 
   */
  public RefTables findByAltKey(UUID idRefTableType, String name) throws Exception {
    
    String sql = String.format("select * from %s.ref_tables " + 
                                 "where id_ref_table_type = ? " + 
                                   "and lower(name) = lower(?)", getSchema());

    try {
      return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new RefTables(rs), idRefTableType, name);
    } 
    catch (EmptyResultDataAccessException e) {
      return null;
    }    
  }  
  /**
   * Insert
   */
  public int insert(RefTables refTables) {
    
    String sql = String.format( 
      "INSERT INTO %s.ref_tables " +
        "(" +
           "id_tenant, " + 
           "id_ref_table_type, " + 
           "name, " +
           "description, " + 
           "value, " + 
           "sequence, " + 
           "created_by, " + 
           "updated_by " +
        ") " +
       "VALUES " +
        "(?,?,?,?,?,?,?,?)", getSchema());

    return jdbcTemplate.update(sql, 
      refTables.getIdTenant(),
      refTables.getIdRefTableType(),
      refTables.getName(), 
      refTables.getDescription(), 
      refTables.getValue(), 
      refTables.getSequence(), 
      refTables.getCreatedBy(),
      refTables.getCreatedBy());
  }
  /**
   * Update
   */
  public int update(RefTables refTables) {
    
    String sql = String.format( 
      "UPDATE %s.ref_tables set " +
        "description = ?, " +
        "value = ?, " +
        "sequence = ?, " +
        "updated_by = ? " +
      "WHERE id = ?", getSchema());

    return jdbcTemplate.update(sql, 
      refTables.getDescription(), 
      refTables.getValue(), 
      refTables.getSequence(), 
      refTables.getUpdatedBy(),
      refTables.getId());
  }
  /**
   * Delete
   */
  public int delete(UUID idRefTableType, String name) throws Exception {
    
    String sql = String.format( 
      "DELETE FROM %s.ref_table_type " +
       "WHERE id_ref_table_type = ? " +
         "AND lower(name) = lower(?)", getSchema());

    return jdbcTemplate.update(sql, idRefTableType, name); 
  }
}