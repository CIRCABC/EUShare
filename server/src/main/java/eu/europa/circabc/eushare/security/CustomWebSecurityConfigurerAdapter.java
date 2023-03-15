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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import eu.europa.circabc.eushare.storage.DBUser;
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
        .antMatchers(HttpMethod.GET, "/admin/**")
        .hasAuthority("ROLE_ADMIN")
        .antMatchers(HttpMethod.GET, "/apikey/**")
        .hasAnyAuthority("ROLE_ADMIN", "API_KEY")
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
