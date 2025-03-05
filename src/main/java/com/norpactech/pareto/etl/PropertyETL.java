package com.norpactech.pareto.etl;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.norpactech.pareto.entity.Property;
import com.norpactech.pareto.entity.PropertyType;
import com.norpactech.pareto.entity.RefTableType;
import com.norpactech.pareto.entity.RefTables;
import com.norpactech.pareto.entity.Schema;
import com.norpactech.pareto.entity.Tenant;
import com.norpactech.pareto.repository.ObjectRepository;
import com.norpactech.pareto.repository.PropertyRepository;
import com.norpactech.pareto.repository.PropertyTypeRepository;
import com.norpactech.pareto.repository.RefTableTypeRepository;
import com.norpactech.pareto.repository.RefTablesRepository;
import com.norpactech.pareto.repository.SchemaRepository;
import com.norpactech.pareto.repository.TenantRepository;
import com.norpactech.pareto.utils.TextUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PropertyETL extends BaseETL {

  @Autowired
  TenantRepository tenantRepository;

  @Autowired
  SchemaRepository schemaRepository;
  
  @Autowired
  ObjectRepository objectRepository;
  
  @Autowired
  PropertyTypeRepository propertyTypeRepository;

  @Autowired
  RefTableTypeRepository refTableTypeRepository;

  @Autowired
  RefTablesRepository refTablesRepository;
  
  @Autowired
  PropertyRepository propertyRepository;

  public void loadData() throws Exception {

    final String RT_DATA_TYPE = "data_type"; // Reference Table 'Data Type'
    
    ClassPathResource resource = new ClassPathResource("etl/Property.csv");
    Path path = Paths.get(resource.getURI());

    int persisted = 0;
    int deleted = 0;

    Reader reader = Files.newBufferedReader(path);
    try (CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
      for (CSVRecord csvRecord : csvParser) {
        if (isComment(csvRecord)) {
          continue;
        }
        Tenant tenant = tenantRepository.findByAltKey(TextUtils.toString(csvRecord.get("tenant")));
        if (tenant == null) {
          log.error("Tenant <" + csvRecord.get("tenant") + "> not found " +
              "for property: " + csvRecord.get("name") + ". Skipping...");
          continue;
        }
        Schema schema = schemaRepository.findByAltKey(tenant.getId(), TextUtils.toString(csvRecord.get("schema")));
        if (schema == null) {
          log.error("Schama <" + csvRecord.get("schema") + "> not found " +
              "for property: " + csvRecord.get("name") + ". Skipping...");
          continue;
        }        
        com.norpactech.pareto.entity.Object object = objectRepository.findByAltKey(schema.getId(), TextUtils.toString(csvRecord.get("object")));
        if (object == null) {
          log.error("Object <" + csvRecord.get("name") + "> not found " + 
               "for property: " + csvRecord.get("name") + ". Skipping...");
          continue;
        }
        Property defaultProperties = null;
        PropertyType propertyType = null;
        String action = csvRecord.get("action").toLowerCase();

        if (action.startsWith("p")) { 
          // Get the default properties
          propertyType = propertyTypeRepository.findByAltKey(tenant.getId(), TextUtils.toString(csvRecord.get("data_type")));
          if (propertyType != null) {
            defaultProperties = new Property();
            defaultProperties.setDataType(propertyType.getName());
            defaultProperties.setLength(propertyType.getLength());
            defaultProperties.setPrecision(propertyType.getPrecision());
            defaultProperties.setIsNullable(propertyType.getIsNullable());
            defaultProperties.setDefaultValue(propertyType.getDefaultValue());
            defaultProperties.setValidation(propertyType.getValidation());
          }
          else {
            // Check if the data type is valid
            RefTableType refTableType = refTableTypeRepository.findByAltKey(tenant.getId(), RT_DATA_TYPE);
            if (refTableType != null) {
              RefTables refTables = refTablesRepository.findByAltKey(refTableType.getId(), TextUtils.toString(csvRecord.get("data_type")));
              
              if (refTables == null) {
                log.error("Reference Table <" + csvRecord.get("property_type") + "> not found " +
                  "for property: " + csvRecord.get("name") + ". Skipping...");
                continue;
              }
            }
          }
        }
        Property property = propertyRepository.findByAltKey(object.getId(), TextUtils.toString(csvRecord.get("name")));
        
        // Persist else delete
        if (action.startsWith("p")) {
          if (property == null) {
            property = new Property();
            property.setIdObject(object.getId());
            property.setName(TextUtils.toString(csvRecord.get("name")));
            property.setSequence(TextUtils.toInteger(csvRecord.get("sequence")));
            property.setDescription(TextUtils.toString(csvRecord.get("description")));            
            property.setDataType(TextUtils.toString(csvRecord.get("data_type")));            
            property.setLength(TextUtils.toInteger(csvRecord.get("length")));            
            property.setPrecision(TextUtils.toInteger(csvRecord.get("precision")));            
            property.setIsNullable(TextUtils.toBoolean(csvRecord.get("is_nullable")));            
            property.setDefaultValue(TextUtils.toString(csvRecord.get("default_value")));            
            property.setValidation(TextUtils.toString(csvRecord.get("validation")));

            // Enrich the Property with defaults
            if (propertyType != null) {
              if (property.getLength() == null) {
                property.setLength(propertyType.getLength());
              }
              if (property.getPrecision() == null) {
                property.setPrecision(propertyType.getPrecision());
              }
              if (property.getIsNullable() == null) {
                property.setIsNullable(propertyType.getIsNullable());
              }
              if (property.getDefaultValue() == null) {
                property.setDefaultValue(propertyType.getDefaultValue());
              }
              if (property.getValidation() == null) {
                property.setValidation(propertyType.getValidation());
              }
            } 
            property.setCreatedBy("etl");
            propertyRepository.insert(property);                    
          }
          else {
            property.setSequence(TextUtils.toInteger(csvRecord.get("sequence")));
            property.setDataType(TextUtils.toString(csvRecord.get("data_type")));
            property.setLength(TextUtils.toInteger(csvRecord.get("length")));            
            property.setPrecision(TextUtils.toInteger(csvRecord.get("precision")));            
            property.setIsNullable(TextUtils.toBoolean(csvRecord.get("is_nullable")));            
            property.setDefaultValue(TextUtils.toString(csvRecord.get("default_value")));            
            property.setValidation(TextUtils.toString(csvRecord.get("validation")));

            // Enrich the Property with defaults
            if (propertyType != null) {
              if (property.getLength() == null) {
                property.setLength(propertyType.getLength());
              }
              if (property.getPrecision() == null) {
                property.setPrecision(propertyType.getPrecision());
              }
              if (property.getIsNullable() == null) {
                property.setIsNullable(propertyType.getIsNullable());
              }
              if (property.getDefaultValue() == null) {
                property.setDefaultValue(propertyType.getDefaultValue());
              }
              if (property.getValidation() == null) {
                property.setValidation(propertyType.getValidation());
              }
            } 
            property.setUpdatedBy("etl2");
            propertyRepository.update(property);
          }
          persisted++;
        }
        else if (action.startsWith("d") && tenant.getId() != null) {
          propertyRepository.delete(object.getId(), TextUtils.toString(csvRecord.get("name")));
          deleted++;
        }
        else {
          log.error("Unknown action <{}> for property: {}. Skipping...", action, TextUtils.toString(csvRecord.get("name")));
        }
      }
    } 
    catch (DataAccessException e) {
      e.printStackTrace();
    }
    log.info("Property ETL Completed with {} persisted and {} deleted", persisted, deleted );
  }
}
