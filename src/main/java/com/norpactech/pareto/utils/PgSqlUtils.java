package com.norpactech.pareto.utils;

import java.util.Map;

public class PgSqlUtils {

  public static String convertType(Map<String, Object> details) {
    
    String type = (String) details.get("type");
    String format = (String) details.get("format");
    Integer maxLength = details.containsKey("maxLength") ? (Integer) details.get("maxLength") : null;

    switch (type) {
      case "integer":
        return (format != null && format.equals("int64")) ? "BIGINT" : "INTEGER";
      case "string":
        if ("date-time".equals(format)) {
          return "TIMESTAMPTZ";
        } 
        else if (maxLength != null) {
          return "VARCHAR(" + maxLength + ")";
        }
        return "TEXT";
      case "boolean":
        return "BOOLEAN";
      case "number":
        return "DOUBLE PRECISION";
      default:
        return "TEXT";
    }
  }
}
