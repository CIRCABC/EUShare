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

import java.sql.Date;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import eu.europa.circabc.eushare.storage.entity.DBUserCreationLog;

public interface UserCreationLogRepository extends JpaRepository<DBUserCreationLog, Integer> {

    Optional<DBUserCreationLog> findByDateCreated(Date dateCreated);

    @Modifying
    @Transactional
    void deleteByDateCreatedBefore(Date thresholdDate);
}
