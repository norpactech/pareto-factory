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

import com.norpactech.pareto.entity.PropertyType;
import com.norpactech.pareto.entity.RefTableType;
import com.norpactech.pareto.entity.RefTables;
import com.norpactech.pareto.entity.Tenant;
import com.norpactech.pareto.repository.PropertyTypeRepository;
import com.norpactech.pareto.repository.RefTableTypeRepository;
import com.norpactech.pareto.repository.RefTablesRepository;
import com.norpactech.pareto.repository.TenantRepository;
import com.norpactech.pareto.utils.TextUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PropertyTypeETL extends BaseETL {

  @Autowired
  TenantRepository tenantRepository;

  @Autowired
  RefTableTypeRepository refTableTypeRepository;
  
  @Autowired
  RefTablesRepository refTablesRepository;
  
  @Autowired
  PropertyTypeRepository propertyTypeRepository;
  
  public void loadData() throws Exception {

    ClassPathResource resource = new ClassPathResource("etl/PropertyType.csv");
    Path path = Paths.get(resource.getURI());

    int persisted = 0;
    int deleted = 0;

    Reader reader = Files.newBufferedReader(path);
    try (CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
      for (CSVRecord csvRecord : csvParser) {
        if (isComment(csvRecord)) {
          continue;
        }
        Tenant tenant = tenantRepository.findByAltKey(csvRecord.get("tenant"));
        if (tenant == null) {
          log.error("Tenant <" + csvRecord.get("tenant") + "> not found " +
              "for property_type: " + csvRecord.get("name") + ". Skipping...");
          continue;
        }
        RefTableType refTableType = refTableTypeRepository.findByAltKey(tenant.getId(), "data_type");
        if (refTableType == null) {
          log.error("Reference Table Type <data_type> not found " +
              "for property_type: " + csvRecord.get("name") + ". Skipping...");
          continue;
        }        
        RefTables refTables = refTablesRepository.findByAltKey(refTableType.getId(), csvRecord.get("data_type"));
        if (refTables == null) {
          log.error("Reference Table Data Type <" + csvRecord.get("data_type") + "> not found " +
              "for property_type: " + csvRecord.get("name") + ". Skipping...");
          continue;
        }        
        PropertyType propertyType = propertyTypeRepository.findByAltKey(refTables.getId(), csvRecord.get("name"));
        String action = csvRecord.get("action").toLowerCase();

        // Persist else delete
        if (action.startsWith("p")) {
          if (propertyType == null) {
            propertyType = new PropertyType();
            propertyType.setIdTenant(tenant.getId());
            propertyType.setIdRtDataType(refTables.getId());
            propertyType.setName(TextUtils.toString(csvRecord.get("name")));
            propertyType.setDescription(TextUtils.toString(csvRecord.get("description")));            
            propertyType.setLength(TextUtils.toInteger(csvRecord.get("length")));            
            propertyType.setPrecision(TextUtils.toInteger(csvRecord.get("precision")));            
            propertyType.setIsNullable(TextUtils.toBoolean(csvRecord.get("is_nullable")));            
            propertyType.setDefaultValue(TextUtils.toString(csvRecord.get("default_value")));            
            propertyType.setValidation(TextUtils.toString(csvRecord.get("validation")));
            propertyType.setCreatedBy("etl");
            propertyTypeRepository.insert(propertyType);                    
          }
          else {
            propertyType.setDescription(TextUtils.toString(csvRecord.get("description")));            
            propertyType.setLength(TextUtils.toInteger(csvRecord.get("length")));            
            propertyType.setPrecision(TextUtils.toInteger(csvRecord.get("precision")));            
            propertyType.setIsNullable(TextUtils.toBoolean(csvRecord.get("is_nullable")));            
            propertyType.setDefaultValue(TextUtils.toString(csvRecord.get("default_value")));            
            propertyType.setValidation(TextUtils.toString(csvRecord.get("validation")));
            propertyType.setUpdatedBy("etl2");
            propertyTypeRepository.update(propertyType);
          }
          persisted++;
        }
        else if (action.startsWith("d") && tenant.getId() != null) {
          propertyTypeRepository.delete(refTables.getId(), csvRecord.get("name"));
          deleted++;
        }
        else {
          log.error("Unknown action <{}> for property_type: {}. Skipping...", action, csvRecord.get("name"));
        }
      }
    } 
    catch (DataAccessException e) {
      e.printStackTrace();
    }
    log.info("Property Type ETL Completed with {} persisted and {} deleted", persisted, deleted );
  }
}
