package com.norpactech.pareto.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.norpactech.pareto.entity.Schema;

@Repository
public class SchemaRepository extends BaseRepository {

  @Autowired
  TenantRepository tenantRepository;
  
  public SchemaRepository(JdbcTemplate jdbcTemplate) {
    super(jdbcTemplate);
  }
  /**
   * Find by alternate key
   * @throws Exception 
   */
  public Schema findByAltKey(UUID idTenant, String name) throws Exception {
    
    String sql = String.format("select * from %s.schema where id_tenant = ? and name = ?", getSchema());

    try {
      return jdbcTemplate.queryForObject(sql, new RowMapper<Schema>() {
        @Override
        public Schema mapRow(ResultSet rs, int rowNum) throws SQLException {
          return new Schema(rs);
        }
      }, idTenant, name);
    }
    catch(EmptyResultDataAccessException e) {
      return null;
    }
  }
  /**
   * Insert
   */
  public int insert(Schema schema) {
    
    String sql = String.format( 
      "INSERT INTO %s.schema " +
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
      schema.getIdTenant(),
      schema.getName(), 
      schema.getDescription(), 
      schema.getCreatedBy(),
      schema.getCreatedBy());
  }
  /**
   * Update
   */
  public int update(Schema schema) {
    
    String sql = String.format( 
      "UPDATE %s.schema set " +
        "id_tenant = ?, " +
        "name = ?, " +
        "description = ?, " +
        "updated_by = ? " +
      "WHERE id = ?", getSchema());

    return jdbcTemplate.update(sql, 
      schema.getIdTenant(),
      schema.getName(), 
      schema.getDescription(), 
      schema.getUpdatedBy(),
      schema.getId());
  }
  /**
   * Delete
   */
  public int delete(UUID idTenant, String name) throws Exception {
    
    String sql = String.format( 
      "DELETE FROM %s.schema " +
       "WHERE id_tenant = ? " +
         "AND name = ?", getSchema());

    return jdbcTemplate.update(sql, idTenant, name); 
  }
}