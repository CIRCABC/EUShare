/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.configuration.cronjob;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;

import javax.persistence.OptimisticLockException;

import org.apache.maven.doxia.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import eu.europa.circabc.eushare.api.FileApiController;
import eu.europa.circabc.eushare.storage.entity.DBCronJobInfo;
import eu.europa.circabc.eushare.storage.repository.CronJobInfoRepository;

@Service
public class CronJobLockService {

    @Autowired
    private CronJobInfoRepository repository;

    private static final Logger log = LoggerFactory.getLogger(
            CronJobLockService.class);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean lockJob(String cronJobName, String cronExpression) {
        try {
            DBCronJobInfo jobInfo = repository.findByCronjobName(cronJobName);
            if (jobInfo == null) {
                jobInfo = new DBCronJobInfo();
                jobInfo.setCronjobName(cronJobName);
            }
            jobInfo.setIsLocked(true);
            jobInfo.setCronjobDelay(cronExpression);
            repository.save(jobInfo);
            return true;
        } catch (ObjectOptimisticLockingFailureException e) {
            log.info("CronJob " + cronJobName + " already running on another server, skipping..");
            return false;
        } catch (OptimisticLockException e) {
            log.info("CronJob " + cronJobName + " already running on another server, skipping..");
            return false;
        } catch (DataIntegrityViolationException e) {
            log.info("CronJob " + cronJobName + " already existing, skipping..");
            return false;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void unlockJob(String cronJobName) {
        DBCronJobInfo jobInfo = repository.findByCronjobName(cronJobName);
        if (jobInfo != null) {
            jobInfo.setIsLocked(false);
            jobInfo.setLastRunDateTime(LocalDateTime.now());
            repository.save(jobInfo);
        }
    }


}
