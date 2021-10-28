/**
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
@ComponentScan({ "eu.europa.circabc.eushare.api" })
@EnableWebMvc
public class UploadConfiguration extends WebMvcConfigurationSupport {

    @Bean(name = "multipartResolver")
    public MultipartResolver multipartResolver(EasyShareConfiguration esConfig) {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(esConfig.getMaxSizeAllowedInBytes());
        multipartResolver.setMaxUploadSizePerFile(esConfig.getMaxSizeAllowedInBytes());
        return multipartResolver;
    }
}