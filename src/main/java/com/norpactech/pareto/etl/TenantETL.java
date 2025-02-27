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

@Service
public class TenantETL {
  
  @Autowired
  TenantRepository tenantRepository;

  public void loadData() throws IOException {

    ClassPathResource resource = new ClassPathResource("etl/Tenant.csv");
    Path path = Paths.get(resource.getURI());

    Reader reader = Files.newBufferedReader(path);
    try (CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
      for (CSVRecord csvRecord : csvParser) {

        Tenant tenant = tenantRepository.findByName(csvRecord.get("name"));
        
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
          tenant.setUpdatedBy("etl2");
          tenantRepository.update(tenant);
          tenantRepository.delete(csvRecord.get("name"));
        }
      }
    } 
    catch (DataAccessException e) {
      e.printStackTrace();
    }
  }
}
