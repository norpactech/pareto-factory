package com.norpactech.pareto.repository;

import java.util.UUID;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.norpactech.pareto.entity.PropertyType;

@Repository
public class PropertyTypeRepository extends BaseRepository {

  public PropertyTypeRepository(JdbcTemplate jdbcTemplate) {
    super(jdbcTemplate);
  }
  /**
   * Find by alternate key
   * @throws Exception 
   */
  public PropertyType findByIdTenantAndName(UUID idTenant, String name) throws Exception {

    String sql = String.format("select * from %s.property_type " +
        "WHERE id_tenant = ? " +
        "AND lower(name) = lower(?)", getSchema());
    
    try {
      return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new PropertyType(rs), idTenant, name);
    } 
    catch (EmptyResultDataAccessException e) {
      return null;
    }    
  }  
  /**
   * Find by alternate key
   * @throws Exception 
   */
  public PropertyType findByAltKey(UUID idRtDataType, String name) throws Exception {

    String sql = String.format("select * from %s.property_type " +
        "WHERE id_rt_data_type = ? " +
        "AND lower(name) = lower(?)", getSchema());
    
    try {
      return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new PropertyType(rs), idRtDataType, name);
    } 
    catch (EmptyResultDataAccessException e) {
      return null;
    }    
  }
  /**
   * Insert
   */
  public int insert(PropertyType propertyType) {

    String sql = String.format( 
        "INSERT INTO %s.property_type " +
            "(" +
            "id_tenant, " + 
            "id_rt_data_type, " + 
            "name, " +
            "description, " +
            "length, " +
            "precision, " +
            "is_nullable, " +
            "default_value, " +
            "validation, " +
            "created_by, " + 
            "updated_by " +
            ") " +
            "VALUES " +
            "(?,?,?,?,?, ?,?,?,?,?, ?)", getSchema());

    return jdbcTemplate.update(sql, 
        propertyType.getIdTenant(),
        propertyType.getIdRtDataType(),
        propertyType.getName(), 
        propertyType.getDescription(), 
        propertyType.getLength(), 
        propertyType.getPrecision(), 
        propertyType.getIsNullable(), 
        propertyType.getDefaultValue(), 
        propertyType.getValidation(), 
        propertyType.getCreatedBy(),
        propertyType.getCreatedBy());
  }
  /**
   * Update
   */
  public int update(PropertyType propertyType) {

    String sql = String.format( 
        "UPDATE %s.property_type set " +
            "description = ?, " +
            "length = ?, " +
            "precision = ?, " +
            "is_nullable = ?, " +
            "default_value = ?, " +
            "validation = ?, " +
            "updated_by = ? " +
            "WHERE id = ?", getSchema());

    return jdbcTemplate.update(sql, 
        propertyType.getDescription(), 
        propertyType.getLength(), 
        propertyType.getPrecision(), 
        propertyType.getIsNullable(), 
        propertyType.getDefaultValue(), 
        propertyType.getValidation(), 
        propertyType.getUpdatedBy(),
        propertyType.getId());
  }
  /**
   * Delete
   */
  public int delete(UUID idRtDataType, String name) throws Exception {

    String sql = String.format( 
        "DELETE FROM %s.property_type " +
            "WHERE id_rt_data_type = ? " +
            "AND lower(name) = lower(?)", getSchema());

    return jdbcTemplate.update(sql, idRtDataType, name); 
  }
}