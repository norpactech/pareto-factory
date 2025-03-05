package com.norpactech.pareto.repository;

import java.util.UUID;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.norpactech.pareto.entity.Property;

@Repository
public class PropertyRepository extends BaseRepository {

  public PropertyRepository(JdbcTemplate jdbcTemplate) {
    super(jdbcTemplate);
  }
  /**
   * Find by alternate key
   * @throws Exception 
   */
  public Property findByAltKey(UUID idObject, String name) throws Exception {

    String sql = String.format("select * from %s.property " +
        "WHERE id_object = ? " +
        "AND lower(name) = lower(?)", getSchema());
    
    try {
      return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Property(rs), idObject, name);
    } 
    catch (EmptyResultDataAccessException e) {
      return null;
    }    
  }
  /**
   * Insert
   */
  public int insert(Property property) {

    String sql = String.format( 
        "INSERT INTO %s.property " +
            "(" +
            "id_object, " + 
            "sequence, " + 
            "name, " +
            "description, " +
            "data_type, " +
            "length, " +
            "precision, " +
            "is_nullable, " +
            "default_value, " +
            "validation, " +
            "created_by, " + 
            "updated_by " +
            ") " +
            "VALUES " +
            "(?,?,?,?,?, ?,?,?,?,?, ?,?)", getSchema());

    return jdbcTemplate.update(sql, 
        property.getIdObject(),
        property.getSequence(),
        property.getName(), 
        property.getDescription(), 
        property.getDataType(),
        property.getLength(), 
        property.getPrecision(), 
        property.getIsNullable(), 
        property.getDefaultValue(), 
        property.getValidation(), 
        property.getCreatedBy(),
        property.getCreatedBy());
  }
  /**
   * Update
   */
  public int update(Property property) {

    String sql = String.format( 
        "UPDATE %s.property set " +
            "sequence = ?, " +
            "description = ?, " +
            "data_type = ?, " +
            "length = ?, " +
            "precision = ?, " +
            "is_nullable = ?, " +
            "default_value = ?, " +
            "validation = ?, " +
            "updated_by = ? " +
            "WHERE id = ?", getSchema());

    return jdbcTemplate.update(sql, 
        property.getSequence(), 
        property.getDescription(), 
        property.getDataType(), 
        property.getLength(), 
        property.getPrecision(), 
        property.getIsNullable(), 
        property.getDefaultValue(), 
        property.getValidation(), 
        property.getUpdatedBy(),
        property.getId());
  }
  /**
   * Delete
   */
  public int delete(UUID idObject, String name) throws Exception {

    String sql = String.format( 
        "DELETE FROM %s.property " +
            "WHERE id_object = ? " +
            "AND lower(name) = lower(?)", getSchema());

    return jdbcTemplate.update(sql, idObject, name); 
  }
}