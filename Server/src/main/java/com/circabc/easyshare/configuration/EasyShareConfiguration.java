/**
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */

package com.circabc.easyshare.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Configuration
@ConfigurationProperties("easyshare")
@Getter
@Setter
public class EasyShareConfiguration {
    @NotNull
    private long defaultUserSpace;

    @NotNull
    private List<String> disks;

    @NotNull
    private int expirationDays;

    @NotNull
    private long maxSizeAllowedInBytes;

    public LocalDate defaultExpirationDate() {
        return LocalDate.now().plusDays(expirationDays);
    }
}
