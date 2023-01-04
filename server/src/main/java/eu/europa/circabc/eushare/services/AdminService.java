/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europa.circabc.eushare.exceptions.UnknownUserException;
import eu.europa.circabc.eushare.exceptions.UserUnauthorizedException;
import eu.europa.circabc.eushare.model.Stat;
import eu.europa.circabc.eushare.storage.StatsRepository;

@Service
public class AdminService implements AdminServiceInterface {

    private Logger log = LoggerFactory.getLogger(FileService.class);

    @Autowired
    private StatsRepository statRepository;

    @Override
    public List<Stat> getStats(Integer year)  {

        log.debug("#######"+year);
        return statRepository
        .findByYear(year)
        .stream()
        .map(dbStat -> dbStat.toStat())
        .collect(Collectors.toList());
    }

}