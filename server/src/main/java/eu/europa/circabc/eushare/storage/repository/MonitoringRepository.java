/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.storage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import eu.europa.circabc.eushare.storage.dto.MonitoringDetailsDTO;
import eu.europa.circabc.eushare.storage.entity.DBMonitoring;

@Repository
public interface MonitoringRepository extends PagingAndSortingRepository<DBMonitoring, String> {

    DBMonitoring findOneById(String id);

    @Query(name = "MonitoringDetailsDTO.findAllDetails", nativeQuery = true)
    public List<MonitoringDetailsDTO> findAllDetails();

}
