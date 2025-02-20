package com.norpactech.pareto.controller.v01;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v01/generator")
public class GeneratorController {

  @GetMapping("/about")
  public String getAbout() {

    return "/v01/generate";
  }
}