package com.norpactech.pareto.service;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Service
public class PgsqlGeneratorService {

  @Autowired
  private Configuration freemarkerConfig;

  public String bootstrap(String owner, String schema) throws Exception {

    Template template = freemarkerConfig.getTemplate("pgsql/bootstrap.ftl");

    Map<String, Object> model = new HashMap<>();
    model.put("OWNER", owner);
    model.put("SCHEMA", schema);

    // Process template
    StringWriter writer = new StringWriter();
    template.process(model, writer);
    return writer.toString();
  }
}
