package com.norpactech.pareto.controller.v01;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.norpactech.pareto.api.DatabaseFactoryControllerAPI;
import com.norpactech.pareto.domain.DatabaseFactoryAboutVO;
import com.norpactech.pareto.service.PgSqlDatabaseFactoryService;

@RestController
@RequestMapping("/pgsql/v01")
public class PgSqlController implements DatabaseFactoryControllerAPI {

  @Autowired
  PgSqlDatabaseFactoryService factoryService;

  @Override
  @GetMapping("/about")
  public DatabaseFactoryAboutVO getAbout() {

    return new DatabaseFactoryAboutVO(
      factoryService.getFactoryName(), 
      factoryService.getFactoryVersion(),
      factoryService.getDatabaseSchema());
  }
}