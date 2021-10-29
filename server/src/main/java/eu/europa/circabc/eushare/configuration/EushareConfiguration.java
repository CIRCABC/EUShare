/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.configuration;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@ConfigurationProperties("eushare")
@EnableScheduling
public class EushareConfiguration {
    @NotNull
    private long defaultUserSpace;

    @NotNull
    private List<String> disks;

    @NotNull
    private int expirationDays;

    @NotNull
    private boolean activateMailService;

    @NotNull
    private long maxSizeAllowedInBytes;

    @NotNull
    private String clientHttpAddress;

    public LocalDate defaultExpirationDate() {
        return LocalDate.now().plusDays(expirationDays);
    }

    @Bean
    public WebMvcConfigurer webConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("*").allowedHeaders("*").allowCredentials(true);
            }
        };
    }

    public long getDefaultUserSpace() {
        return defaultUserSpace;
    }

    public void setDefaultUserSpace(long defaultUserSpace) {
        this.defaultUserSpace = defaultUserSpace;
    }

    public List<String> getDisks() {
        return disks;
    }

    public void setDisks(List<String> disks) {
        this.disks = disks;
    }

    public int getExpirationDays() {
        return expirationDays;
    }

    public void setExpirationDays(int expirationDays) {
        this.expirationDays = expirationDays;
    }

    public boolean isActivateMailService() {
        return activateMailService;
    }

    public void setActivateMailService(boolean activateMailService) {
        this.activateMailService = activateMailService;
    }

    public long getMaxSizeAllowedInBytes() {
        return maxSizeAllowedInBytes;
    }

    public void setMaxSizeAllowedInBytes(long maxSizeAllowedInBytes) {
        this.maxSizeAllowedInBytes = maxSizeAllowedInBytes;
    }

    public String getClientHttpAddress() {
        return clientHttpAddress;
    }

    public void setClientHttpAddress(String clientHttpAddress) {
        this.clientHttpAddress = clientHttpAddress;
    }
    
}
