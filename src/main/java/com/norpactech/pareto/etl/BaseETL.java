package com.norpactech.pareto.etl;

import org.apache.commons.csv.CSVRecord;
import org.springframework.util.StringUtils;

public abstract class BaseETL {

  public boolean isComment(CSVRecord csvRecord) {

    String start = null;
    try {
      start = csvRecord.get(0);

      if(!StringUtils.hasText(start) ) {
        throw new RuntimeException("Record invalid - col(0) is empty");
      }
      return start.startsWith("//");
    }
    catch (Exception e) {
      throw new RuntimeException("Record col(0) is empty or currupt");
    }
  } 

}
