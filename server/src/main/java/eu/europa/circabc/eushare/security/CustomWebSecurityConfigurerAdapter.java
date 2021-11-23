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


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity(debug = true)
public class CustomWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //@formatter:off
        http.cors().and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and().csrf().disable()                                                // Disabling CSRF verification, a user can modify the content
        .authorizeRequests()
        .antMatchers(HttpMethod.OPTIONS,"/**").permitAll()                      // Authorizing login by anyone, after authentication or not
        .antMatchers(HttpMethod.GET,"/file/{.+}").anonymous()      
        .antMatchers(HttpMethod.GET,"/file/{.+}/fileInfo").anonymous()                 // If an Authorization Header is present, will return 403
        //.antMatchers(HttpMethod.GET,"/users/userInfo").hasAuthority("ROLE_ADMIN") // For later use, when opaqueToken will support authentication converter
        .antMatchers(HttpMethod.PUT, "/user/userInfo").hasAuthority("ROLE_ADMIN")
        .anyRequest().authenticated()     
        .and().exceptionHandling().accessDeniedHandler(accessDeniedHandler())                          // Forces successful authentication for the rest of the mapping
        .and()
            .oauth2ResourceServer()                                 // Set up of a OAuth Resource server
            .authenticationEntryPoint(authenticationEntryPoint())
            .opaqueToken();
            //.jwt().jwtAuthenticationConverter(jwtAuthenticationConverter())// Conversion of the token to a DB user
            //.and()
        //@formatter:on
    }

    
    @Bean
    AuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

    @Bean
    AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }
}
