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

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import eu.europa.circabc.eushare.configuration.EushareConfiguration;
import eu.europa.circabc.eushare.storage.repository.UserRepository;

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
      "/abuse",
      "/abuse/**"
  };

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private EushareConfiguration esConfig;

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    APIKeyAuthenticationFilter apiKeyFilter = new APIKeyAuthenticationFilter(userRepository);

    String allowedOrigin = esConfig.getClientHttpAddress();
    if (allowedOrigin != null) {
      allowedOrigin = allowedOrigin.replace("/share", "");
    }
    RefererFilter refererFilter = new RefererFilter(allowedOrigin);

    http
        .csrf().disable()
        .cors().configurationSource(corsConfigurationSource(allowedOrigin))
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        .antMatchers(AUTH_WHITELIST).permitAll()
        .antMatchers(HttpMethod.HEAD, "/file/{.+}").anonymous()
        .antMatchers(HttpMethod.GET, "/file/{.+}").anonymous()
        .antMatchers(HttpMethod.GET, "/file/{.+}/fileInfo").anonymous()
        .antMatchers(HttpMethod.POST, "/abuse").permitAll()
        .antMatchers(HttpMethod.PUT, "/user/userInfo").hasAuthority("ROLE_ADMIN")
        .anyRequest().authenticated()
        .and()
        .addFilter(apiKeyFilter)
        .addFilterAfter(refererFilter,APIKeyAuthenticationFilter.class)
        .addFilterAfter(refererFilter, APIKeyAuthenticationFilter.class)
        .exceptionHandling()
        .accessDeniedHandler(accessDeniedHandler())
        .and()
        .oauth2ResourceServer()
        .withObjectPostProcessor(new ObjectPostProcessor<BearerTokenAuthenticationFilter>() {
          @Override
          public <O extends BearerTokenAuthenticationFilter> O postProcess(O object) {
            object.setAuthenticationFailureHandler(authenticationFailureHandler());
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

  @Bean
  public CorsConfigurationSource corsConfigurationSource(String allowedOrigin) {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(allowedOrigin));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }

}
