package com.camunda.consulting.batch_integration_test;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableProcessApplication("simple-spring-boot-process-app")
public class CamundaApplication {
  
  private static final Logger LOG = LoggerFactory.getLogger(CamundaApplication.class);

  public static void main(String... args) {
    SpringApplication.run(CamundaApplication.class, args);
  }
  
}
