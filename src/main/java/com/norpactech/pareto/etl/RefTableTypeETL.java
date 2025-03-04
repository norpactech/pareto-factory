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

import com.norpactech.pareto.entity.RefTableType;
import com.norpactech.pareto.entity.Tenant;
import com.norpactech.pareto.repository.RefTableTypeRepository;
import com.norpactech.pareto.repository.TenantRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RefTableTypeETL {
  
  @Autowired
  TenantRepository tenantRepository;

  @Autowired
  RefTableTypeRepository refTableTypeRepository;
  
  public void loadData() throws Exception {

    ClassPathResource resource = new ClassPathResource("etl/RefTableType.csv");
    Path path = Paths.get(resource.getURI());

    int persisted = 0;
    int deleted = 0;

    Reader reader = Files.newBufferedReader(path);
    try (CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
      for (CSVRecord csvRecord : csvParser) {
        Tenant tenant = tenantRepository.findByAltKey(csvRecord.get("tenant"));
        if (tenant == null) {
          log.error("Tenant <" + csvRecord.get("tenant") + "> not found " +
              "for ref_table_type: " + csvRecord.get("name") + ". Skipping...");
          continue;
        }
        RefTableType refTableType = refTableTypeRepository.findByAltKey(tenant.getId(),csvRecord.get("name"));
        String action = csvRecord.get("action").toLowerCase();

        // Persist else delete
        if (action.startsWith("p")) {
          if (refTableType == null) {
            refTableType = new RefTableType();
            refTableType.setIdTenant(tenant.getId());
            refTableType.setName(csvRecord.get("name"));
            refTableType.setDescription(csvRecord.get("description"));
            refTableType.setCreatedBy("etl");
            refTableTypeRepository.insert(refTableType);                    
          }
          else {
            refTableType.setDescription(csvRecord.get("description"));
            refTableType.setUpdatedBy("etl2");
            refTableTypeRepository.update(refTableType);
          }
          persisted++;
        }
        else if (action.startsWith("d") && refTableType != null) {
          refTableTypeRepository.delete(tenant.getId(), csvRecord.get("name"));
          deleted++;
        }
        else if (action.startsWith("//")) {
          continue; // Skip comments
        }
        else {
          log.error("Unknown action <{}> for user: {}. Skipping...", action, csvRecord.get("username"));
        }
      }
    } 
    catch (DataAccessException e) {
      e.printStackTrace();
    }
    log.info("Ref Table Type ETL Completed with {} persisted and {} deleted", persisted, deleted );
  }
}
