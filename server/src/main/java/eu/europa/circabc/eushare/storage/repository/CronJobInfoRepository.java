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

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import eu.europa.circabc.eushare.storage.entity.DBCronJobInfo;

@Repository
public interface CronJobInfoRepository extends JpaRepository<DBCronJobInfo, Long> {

   // @Lock(LockModeType.PESSIMISTIC_WRITE)
    DBCronJobInfo findByCronjobName(String cronjobName);
}

