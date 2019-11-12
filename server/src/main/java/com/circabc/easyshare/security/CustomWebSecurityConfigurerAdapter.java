/**
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */

package com.circabc.easyshare.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.circabc.easyshare.services.UserService;
import com.circabc.easyshare.security.CustomAuthenticationEntryPoint;
import com.circabc.easyshare.security.CustomAccessDeniedHandler;

@Configuration
@EnableWebSecurity(debug = true)
public class CustomWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //@formatter:off
        http.cors().and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and().csrf().disable()                                                // Disabling CSRF verification, a user can modify the content
        .authorizeRequests()
        .antMatchers(HttpMethod.OPTIONS,"/**").permitAll()                      // Authorizing login by anyone, after authentication or not
        .antMatchers(HttpMethod.GET,"/file/{.+}").anonymous()                   // If an Authorization Header is present, will return 403
        .antMatchers(HttpMethod.GET,"/users/userInfo").hasAuthority("ROLE_ADMIN")
        .antMatchers(HttpMethod.PUT, "/user/userInfo").hasAuthority("ROLE_ADMIN")
        .anyRequest().authenticated()                               // Forces successful authentication for the rest of the mapping
        .and()
            .oauth2ResourceServer()                                         // Set up of a OAuth Resource server
            .authenticationEntryPoint(authenticationEntryPoint())
            .jwt().jwtAuthenticationConverter(jwtAuthenticationConverter()) // Conversion of the token to a DB user
            .and()
        .and().exceptionHandling().accessDeniedHandler(accessDeniedHandler());
        //@formatter:on
    }

    @Bean
    AuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

    @Bean
    OIDCJwtAuthenticationConverter jwtAuthenticationConverter() {
        return new OIDCJwtAuthenticationConverter(userService);
    }

    @Bean
    AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

}
