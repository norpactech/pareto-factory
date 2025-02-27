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

import com.norpactech.pareto.entity.User;
import com.norpactech.pareto.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserETL {
  
  @Autowired
  UserRepository userRepository;

  public void loadData() throws IOException {

    ClassPathResource resource = new ClassPathResource("etl/User.csv");
    Path path = Paths.get(resource.getURI());

    Reader reader = Files.newBufferedReader(path);
    
    int persisted = 0;
    int deleted = 0;
    
    try (CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
      for (CSVRecord csvRecord : csvParser) {

        User user = userRepository.findByAltKey(csvRecord.get("username"));
        String action = csvRecord.get("action").toLowerCase();

        // Persist else delete
        if (action.startsWith("p")) {
          if (user == null) {
            user = new User();
            user.setUsername(csvRecord.get("username"));
            user.setEmail(csvRecord.get("email"));
            user.setFullName(csvRecord.get("full_name"));
            user.setCreatedBy("etl");
            user.setUpdatedBy("etl");
            userRepository.insert(user);                    
          }
          else {
            user.setEmail(csvRecord.get("email"));
            user.setFullName(csvRecord.get("full_name"));
            user.setUpdatedBy("etl");
            userRepository.update(user);
          }
          persisted++;
        }
        else if (action.startsWith("d") && user != null) {
          userRepository.delete(csvRecord.get("username"));
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
    log.info("User ETL Completed with {} persisted and {} deleted", persisted, deleted );
  }
}
