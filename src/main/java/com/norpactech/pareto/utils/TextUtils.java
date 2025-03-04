package com.norpactech.pareto.utils;

import com.nimbusds.oauth2.sdk.util.StringUtils;

public class TextUtils {

  public static String toString(String source) {

    if (StringUtils.isBlank(source)) {
      return null;
    }
    return source.trim();
  }
    
  public static Integer toInteger(String source) {

    if (StringUtils.isBlank(source)) {
      return null;
    }
    try {
      return Integer.parseInt(source);
    } 
    catch (NumberFormatException e) {
      throw new RuntimeException("Invalid integer: " + source);
    }
  }
  
  public static Boolean toBoolean(String source) {

    if (StringUtils.isBlank(source)) {
      return null;
    }

    String value = source.toLowerCase();
    if (value.equals("true")) {
      return true;
    }
    else if (value.equals("false")) {
      return false;
    }
    else {
      throw new RuntimeException("Invalid boolean: " + source);
    }
  }  
}
