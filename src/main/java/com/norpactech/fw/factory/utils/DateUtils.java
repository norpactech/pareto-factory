package com.norpactech.fw.factory.utils;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

  public static DateTimeFormatter toDayMinuteSeconds = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss Z");
  
  public static String formatDayMinutesSeconds(ZonedDateTime zonedDateTime) {

    if (zonedDateTime == null) {
      return "null";
    }
    return toDayMinuteSeconds.format(zonedDateTime);
  }
  
  public static Instant toInstant(java.sql.Date source) {
    
    if (source == null) {
      return null;
    }
    return Instant.ofEpochMilli(source.getTime());
  }
}
