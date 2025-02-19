package com.norpactech.pareto.config.tenant;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Configuration
public class MultitenantConfiguration {

  @Value("${pareto-factory.tenant}")
  String defaultTenant;

  @Value("${spring.profiles.active}")
  String springProfilesActive;
  
  @Bean
  @ConfigurationProperties(prefix = "tenants")
  DataSource dataSource() throws Exception {
    
    String env = null;
    if (springProfilesActive.contains("local-")) {
      env = "-local-";
    }
    else if (springProfilesActive.contains("docker-")) {
      env = "-docker-";
    }
    else if (springProfilesActive.contains("dev-")) {
      env = "-dev-";
    }
    else if (springProfilesActive.contains("prod-")) {
      env = "-prod-";
    }
    else {
      throw new Exception("Active Profiles for must contain local-, docker-, dev-, or prod-. i.e. 'prod-[tenant]'. The following was given: " + springProfilesActive);
    }
    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    Resource[] resources = resolver.getResources("classpath:/*" + env + "*.yml");
    Map<Object, Object> resolvedDataSources = new HashMap<>();

    for (Resource resource : resources) {
      Properties tenantProperties = loadYamlIntoProperties(resource);
      DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
      String tenant = tenantProperties.getProperty("tenant.name");
        
      if (tenant != null) {        
        dataSourceBuilder.driverClassName(tenantProperties.getProperty("spring.datasource.driver-class-name"));
        dataSourceBuilder.username(tenantProperties.getProperty("spring.datasource.username"));
        dataSourceBuilder.password(tenantProperties.getProperty("spring.datasource.password"));
        dataSourceBuilder.url(tenantProperties.getProperty("spring.datasource.url"));
        resolvedDataSources.put(tenant, dataSourceBuilder.build());
      }
    }
    AbstractRoutingDataSource dataSource = new MultitenantDataSource();
    dataSource.setDefaultTargetDataSource(resolvedDataSources.get(defaultTenant));
    dataSource.setTargetDataSources(resolvedDataSources);

    dataSource.afterPropertiesSet();
    return dataSource;
  }
  
  @Bean
  JdbcTemplate jdbcTemplate(DataSource dataSource) {
      return new JdbcTemplate(dataSource);
  }  
  private Properties loadYamlIntoProperties(Resource resource) throws IOException {
    YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
    factory.setResources(resource);
    return factory.getObject();
  }
}