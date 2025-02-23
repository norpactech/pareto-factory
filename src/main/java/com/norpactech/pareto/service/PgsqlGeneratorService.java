package com.norpactech.pareto.service;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.norpactech.pareto.domain.AttributeVO;
import com.norpactech.pareto.domain.DeclarationValidationVO;
import com.norpactech.pareto.domain.FunctionValidationVO;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Service
public class PgsqlGeneratorService {

  @Autowired
  private Configuration freemarkerConfig;

  public String bootstrap(String owner, String schema) throws Exception {

    Template template = freemarkerConfig.getTemplate("pgsql/bootstrap.ftl");

    Map<String, Object> model = new HashMap<>();
    model.put("owner", owner);
    model.put("schema", schema);

    // Process template
    StringWriter writer = new StringWriter();
    template.process(model, writer);
    return writer.toString();
  }
  /**
   * Create validations
   */
  public String validations(List<DeclarationValidationVO> validations) throws Exception {

    Template template = freemarkerConfig.getTemplate("pgsql/validation.ftl");

    Map<String, Object> model = new HashMap<>();
    model.put("validations", validations);

    // Process template
    StringWriter writer = new StringWriter();
    template.process(model, writer);
    return writer.toString();
  }    
  /**
   * Create (Insert) an object
   */
  public String create(
      String schema, 
      String object, 
      boolean hasAudit, 
      List<AttributeVO> attributes, 
      List<FunctionValidationVO> validations) throws Exception {

    Template template = freemarkerConfig.getTemplate("pgsql/insert.ftl");

    Map<String, Object> model = new HashMap<>();
    model.put("schema", schema);
    model.put("object", object);
    model.put("hasAudit", hasAudit);
    model.put("attributes", attributes);
    model.put("validations", validations);

    // Process template
    StringWriter writer = new StringWriter();
    template.process(model, writer);
    return writer.toString();
  }   
  /**
   * Function to Update a ROW
   */
  public String update(
      String schema, 
      String object, 
      boolean hasAudit, 
      List<AttributeVO> attributes, 
      List<FunctionValidationVO> validations) throws Exception {

    Template template = freemarkerConfig.getTemplate("pgsql/update.ftl");

    Map<String, Object> model = new HashMap<>();
    model.put("schema", schema);
    model.put("object", object);
    model.put("hasAudit", hasAudit);
    model.put("attributes", attributes);
    model.put("validations", validations);

    // Process template
    StringWriter writer = new StringWriter();
    template.process(model, writer);
    return writer.toString();
  }
  /**
   * Functions to Deactivate/Reactivate (Soft Delete) an object
   */
  public String isActive(String schema, String object) throws Exception {

    Template template = freemarkerConfig.getTemplate("pgsql/active.ftl");

    Map<String, Object> model = new HashMap<>();
    model.put("schema", schema);
    model.put("object", object);

    // Process template
    StringWriter writer = new StringWriter();
    template.process(model, writer);
    return writer.toString();
  }  
  
  /**
   * Function to Delete a ROW
   */
  public String delete(String schema, String object) throws Exception {

    Template template = freemarkerConfig.getTemplate("pgsql/delete.ftl");

    Map<String, Object> model = new HashMap<>();
    model.put("schema", schema);
    model.put("object", object);

    // Process template
    StringWriter writer = new StringWriter();
    template.process(model, writer);
    return writer.toString();
  }  
}
