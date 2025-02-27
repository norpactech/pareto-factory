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
import com.norpactech.pareto.repository.SchemaRepository;
import com.norpactech.pareto.repository.TenantRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SchemaETL {
  
  @Autowired
  SchemaRepository schemaRepository;
  
  @Autowired
  TenantRepository tenantRepository;

  public void loadData() throws Exception {

    ClassPathResource resource = new ClassPathResource("etl/Schema.csv");
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
        Schema schema = schemaRepository.findByAltKey(tenant.getId(),csvRecord.get("name"));
        String action = csvRecord.get("action").toLowerCase();

        // Persist else delete
        if (action.startsWith("p")) {
          if (schema == null) {
            schema = new Schema();
            schema.setIdTenant(tenant.getId());
            schema.setName(csvRecord.get("name"));
            schema.setDescription(csvRecord.get("description"));
            schema.setCreatedBy("etl");
            schemaRepository.insert(schema);                    
          }
          else {
            schema.setDescription(csvRecord.get("description"));
            schema.setUpdatedBy("etl2");
            schemaRepository.update(schema);
            schemaRepository.delete(tenant.getId(), csvRecord.get("name"));          
          }
          persisted++;
        }
        else if (action.startsWith("d") && schema != null) {
          schemaRepository.delete(tenant.getId(), csvRecord.get("name"));
          deleted++;
        }
        else {
          log.error("Unknown action <{}> for user: {}. Skipping...", action, csvRecord.get("username"));
        }
      }
    } 
    catch (DataAccessException e) {
      e.printStackTrace();
    }
    log.info("Schema ETL Completed with {} persisted and {} deleted", persisted, deleted );
  }
}
