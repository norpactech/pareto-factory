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
import com.norpactech.pareto.entity.RefTables;
import com.norpactech.pareto.entity.Tenant;
import com.norpactech.pareto.repository.RefTableTypeRepository;
import com.norpactech.pareto.repository.RefTablesRepository;
import com.norpactech.pareto.repository.TenantRepository;
import com.norpactech.pareto.utils.TextUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RefTablesETL extends BaseETL {

  @Autowired
  TenantRepository tenantRepository;

  @Autowired
  RefTableTypeRepository refTableTypeRepository;
  
  @Autowired
  RefTablesRepository refTablesRepository;

  public void loadData() throws Exception {

    ClassPathResource resource = new ClassPathResource("etl/RefTables.csv");
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
              "for ref_tables: " + csvRecord.get("name") + ". Skipping...");
          continue;
        }
        RefTableType refTableType = refTableTypeRepository.findByAltKey(tenant.getId(), TextUtils.toString(csvRecord.get("ref_table_type")));
        if (refTableType == null) {
          log.error("Reference Table Type <" + csvRecord.get("ref_table_type") + "> not found " +
              "for ref_tables: " + csvRecord.get("name") + ". Skipping...");
          continue;
        }        
        RefTables refTables = refTablesRepository.findByAltKey(refTableType.getId(), TextUtils.toString(csvRecord.get("name")));
        String action = csvRecord.get("action").toLowerCase();

        // Persist else delete
        if (action.startsWith("p")) {
          if (refTables == null) {
            refTables = new RefTables();
            refTables.setIdTenant(tenant.getId());
            refTables.setIdRefTableType(refTableType.getId());
            refTables.setName(TextUtils.toString(csvRecord.get("name")));
            refTables.setDescription(TextUtils.toString(csvRecord.get("description")));            
            refTables.setValue(TextUtils.toString(csvRecord.get("value"))); 
            refTables.setSequence(TextUtils.toInteger(csvRecord.get("sequence")));            
            refTables.setCreatedBy("etl");
            refTablesRepository.insert(refTables);                    
          }
          else {
            refTables.setDescription(TextUtils.toString(csvRecord.get("description")));
            refTables.setValue(TextUtils.toString(csvRecord.get("value")));            
            refTables.setSequence(TextUtils.toInteger(csvRecord.get("sequence")));            
            refTables.setUpdatedBy("etl2");
            refTablesRepository.update(refTables);
          }
          persisted++;
        }
        else if (action.startsWith("d") && refTableType != null) {
          refTablesRepository.delete(refTableType.getId(), TextUtils.toString(csvRecord.get("name")));
          deleted++;
        }
        else {
          log.error("Unknown action <{}> for ref_tables: {}. Skipping...", action, TextUtils.toString(csvRecord.get("name")));
        }
      }
    } 
    catch (DataAccessException e) {
      e.printStackTrace();
    }
    log.info("Reference Tables ETL Completed with {} persisted and {} deleted", persisted, deleted );
  }
}
