package com.norpactech.pareto.etl;

import java.io.IOException;
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

import com.norpactech.pareto.entity.Tenant;
import com.norpactech.pareto.repository.TenantRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TenantETL extends BaseETL {
  
  @Autowired
  TenantRepository tenantRepository;

  public void loadData() throws IOException {

    ClassPathResource resource = new ClassPathResource("etl/Tenant.csv");
    Path path = Paths.get(resource.getURI());

    int persisted = 0;
    int deleted = 0;
    
    Reader reader = Files.newBufferedReader(path);
    try (CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
      for (CSVRecord csvRecord : csvParser) {
        if (isComment(csvRecord)) {
          continue;
        }
        Tenant tenant = tenantRepository.findByAltKey(csvRecord.get("name"));
        String action = csvRecord.get("action").toLowerCase();

        // Persist else delete
        if (action.startsWith("p")) {
          if (tenant == null) {
            tenant = new Tenant();
            tenant.setName(csvRecord.get("name"));
            tenant.setDescription(csvRecord.get("description"));
            tenant.setCopyright(csvRecord.get("copyright"));
            tenant.setCreatedBy("etl");
            tenantRepository.insert(tenant);                    
          }
          else {
            tenant.setDescription(csvRecord.get("description"));
            tenant.setCopyright(csvRecord.get("copyright"));
            tenant.setUpdatedBy("etl");
            tenantRepository.update(tenant);
          }
          persisted++;
        }
        else if (action.startsWith("d") && tenant != null) {
          tenantRepository.delete(csvRecord.get("name"));
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
    log.info("Tenant ETL Completed with {} persisted and {} deleted", persisted, deleted );
  }
}
