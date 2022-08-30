/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare;

import com.fasterxml.jackson.databind.Module;
import eu.europa.circabc.eushare.services.UserService;
import javax.annotation.PostConstruct;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class EushareApplication extends SpringBootServletInitializer {

  @Override
  protected SpringApplicationBuilder configure(
    SpringApplicationBuilder builder
  ) {
    return builder.sources(EushareApplication.class);
  }

  @Autowired
  UserService userService;

  @PostConstruct
  public void initUsers() {
    userService.setAdminUsers();
  }

  public static void main(String[] args) {
    new SpringApplication(EushareApplication.class).run(args);
  }

  @Bean
  public Module jsonNullableModule() {
    return new JsonNullableModule();
  }
}
