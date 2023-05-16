/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import eu.europa.circabc.eushare.storage.UserRepository;

@Configuration
@EnableWebSecurity(debug = true)
public class CustomWebSecurityConfigurerAdapter
    extends WebSecurityConfigurerAdapter {

  private static final String[] AUTH_WHITELIST = {
      "/api-docs",
      "/api-docs/**",
      "/swagger-resources",
      "/swagger-resources/**",
      "/swagger-ui",
      "/swagger-ui/**",
      "/jmd/",
      "/jmd/**",
  };

  @Autowired
  private UserRepository userRepository;

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    APIKeyAuthenticationFilter filter = new APIKeyAuthenticationFilter(userRepository);
    

    http
        .cors()
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()

        .csrf()
        .disable()
        .authorizeRequests()
        .antMatchers(HttpMethod.OPTIONS, "/**")
        .permitAll()
        .antMatchers(AUTH_WHITELIST)
        .permitAll()
        .antMatchers(HttpMethod.HEAD, "/file/{.+}")
        .anonymous()
        .antMatchers(HttpMethod.GET, "/file/{.+}")
        .anonymous()
        .antMatchers(HttpMethod.GET, "/file/{.+}/fileInfo")
        .anonymous()
        .antMatchers(HttpMethod.PUT, "/user/userInfo")
        .hasAuthority("ROLE_ADMIN")
        .anyRequest()
        .authenticated()
        .and()
        .addFilter(filter)
        .exceptionHandling()
        .accessDeniedHandler(accessDeniedHandler())
        .and()
        .oauth2ResourceServer()
        .withObjectPostProcessor(
            new ObjectPostProcessor<BearerTokenAuthenticationFilter>() {
              @Override
              public <O extends BearerTokenAuthenticationFilter> O postProcess(
                  O object) {
                object.setAuthenticationFailureHandler(
                    authenticationFailureHandler());
                return object;
              }
            })
        .authenticationEntryPoint(authenticationEntryPoint())
        .opaqueToken();
  }

  @Bean
  AuthenticationEntryPoint authenticationEntryPoint() {
    return new CustomAuthenticationEntryPoint();
  }

  @Bean
  AccessDeniedHandler accessDeniedHandler() {
    return new CustomAccessDeniedHandler();
  }

  @Bean
  AuthenticationFailureHandler authenticationFailureHandler() {
    return new CustomAuthenticationHandler();
  }
}
