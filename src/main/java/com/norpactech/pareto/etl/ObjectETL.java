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

import com.norpactech.pareto.entity.Schema;
import com.norpactech.pareto.entity.Tenant;
import com.norpactech.pareto.repository.ObjectRepository;
import com.norpactech.pareto.repository.SchemaRepository;
import com.norpactech.pareto.repository.TenantRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ObjectETL {

  @Autowired
  TenantRepository tenantRepository;

  @Autowired
  SchemaRepository schemaRepository;
  
  @Autowired
  ObjectRepository objectRepository;

  public void loadData() throws Exception {

    ClassPathResource resource = new ClassPathResource("etl/Object.csv");
    Path path = Paths.get(resource.getURI());

    int persisted = 0;
    int deleted = 0;

    Reader reader = Files.newBufferedReader(path);
    try (CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
      for (CSVRecord csvRecord : csvParser) {
        Tenant tenant = tenantRepository.findByAltKey(csvRecord.get("tenant"));
        if (tenant == null) {
          log.error("Tenant <" + csvRecord.get("tenant") + "> not found " +
              "for schema: " + csvRecord.get("name") + ". Skipping...");
          continue;
        }
        Schema schema = schemaRepository.findByAltKey(tenant.getId(), csvRecord.get("schema"));
        if (schema == null) {
          log.error("Schama <" + csvRecord.get("schema") + "> not found " +
              "for object: " + csvRecord.get("name") + ". Skipping...");
          continue;
        }        
        com.norpactech.pareto.entity.Object object = objectRepository.findByAltKey(schema.getId(),csvRecord.get("name"));
        String action = csvRecord.get("action").toLowerCase();

        // Persist else delete
        if (action.startsWith("p")) {
          if (object == null) {
            object = new com.norpactech.pareto.entity.Object();
            object.setIdSchema(schema.getId());
            object.setName(csvRecord.get("name"));
            object.setDescription(csvRecord.get("description"));            
            object.setHasIdentifier(csvRecord.get("has_identifier").equals("TRUE") ? true : false);
            object.setHasAudit(csvRecord.get("has_audit").equals("TRUE") ? true : false);
            object.setCreatedBy("etl");
            objectRepository.insert(object);                    
          }
          else {
            object.setDescription(csvRecord.get("description"));
            object.setHasIdentifier(csvRecord.get("has_identifier").equals("TRUE") ? true : false);
            object.setHasAudit(csvRecord.get("has_audit").equals("TRUE") ? true : false);
            object.setUpdatedBy("etl2");
            objectRepository.update(object);
          }
          persisted++;
        }
        else if (action.startsWith("d") && schema != null) {
          objectRepository.delete(schema.getId(), csvRecord.get("name"));
          deleted++;
        }
        else if (action.startsWith("//")) {
          continue; // Skip comments
        }
        else {
          log.error("Unknown action <{}> for user: {}. Skipping...", action, csvRecord.get("name"));
        }
      }
    } 
    catch (DataAccessException e) {
      e.printStackTrace();
    }
    log.info("Object ETL Completed with {} persisted and {} deleted", persisted, deleted );
  }
}
