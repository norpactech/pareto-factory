package com.norpactech.pareto.controller.v01;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.norpactech.pareto.config.tenant.TenantContext;
import com.norpactech.pareto.domain.AboutVO;
import com.norpactech.pareto.utils.DateUtils;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/v01")
public class CommonsController {
	
  private static ZonedDateTime zonedDateTime = Instant.now().atZone(ZoneId.systemDefault());    
    
  @Value("${spring.application.name}")
  private String name;
    
  @Value("${pareto-factory.version}")
  private String version;

  @Value("${spring.profiles.active}")
  private String profiles;

  @Autowired
  private DataSource dataSource;
  
  @Value("${pareto-factory.tenant}")
  String defaultTenant;
  
  @PostConstruct
  public void init() {
    TenantContext.setCurrentTenant(defaultTenant);
  }

  @GetMapping("/health")
  public Map<String, String> health() {
    Map<String, String> status = new HashMap<>();
    status.put("status", "OK");
    return status;
  }
  
  @GetMapping("/about")
  public AboutVO about() throws Exception { 
    
    String started = DateUtils.formatDayMinutesSeconds(zonedDateTime);
    String authorities = "unavailable";

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      authorities = authentication.getAuthorities().toString();
    }    
    String databaseSchema = dataSource.getConnection().getCatalog();

    AboutVO aboutVO = new AboutVO();
    aboutVO.setApplication(name);
    aboutVO.setVersion(version);
    aboutVO.setProfiles(profiles);
    aboutVO.setStarted(started);
    aboutVO.setAuthorities(authorities);
    aboutVO.setDatabaseSchema(databaseSchema);
    return aboutVO;
  }
}