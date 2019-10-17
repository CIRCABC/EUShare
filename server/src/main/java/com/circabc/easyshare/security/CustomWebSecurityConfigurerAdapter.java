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

import com.circabc.easyshare.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.openid.OpenIDAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity(debug = true)
public class CustomWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        //@formatter:off
        http
        .csrf().disable()                                                // Disabling CSRF verification, a user can modify the content
        .authorizeRequests()
        .antMatchers("/login").permitAll()                    // Authorizing login by anyone, after authentication or not
        .antMatchers(HttpMethod.OPTIONS,"/**").permitAll()    // Authorizing login by anyone, after authentication or not
        .antMatchers(HttpMethod.GET,"/file/{.+}").anonymous() // If an Authorization Header is present, will return 403
        .antMatchers(HttpMethod.GET,"/users/userInfo").hasRole("ADMIN")
        .antMatchers(HttpMethod.PUT, "/user/userInfo").hasRole("ADMIN")
        .anyRequest().authenticated()                                   // Forces successful authentication for the rest of the mapping
        .and()/*..oauth2ResourceServer()..openidLogin().loginProcessingUrl("/login")*/.httpBasic().authenticationEntryPoint(authenticationEntryPoint())
        .and().exceptionHandling().accessDeniedHandler(accessDeniedHandler())
        .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        //@formatter:on
    }

    @Bean
    AuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(this.userService);
        return daoAuthenticationProvider;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    /**
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.GET,"/file/{.+}");
    }**/


}
