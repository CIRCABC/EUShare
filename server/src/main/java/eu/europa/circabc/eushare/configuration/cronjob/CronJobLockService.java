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

import java.net.InetAddress;
import java.time.LocalDateTime;

import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;

import org.hibernate.StaleStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import eu.europa.circabc.eushare.storage.entity.DBCronJobInfo;
import eu.europa.circabc.eushare.storage.repository.CronJobInfoRepository;

@Service
public class CronJobLockService {

    @Autowired
    private CronJobInfoRepository repository;

    private static final Logger log = LoggerFactory.getLogger(
            CronJobLockService.class);

    public boolean lockJob(String cronJobName, String cronExpression) {
        try {
            String serverId = getServerHostname();
            DBCronJobInfo jobInfo = repository.findByCronjobName(cronJobName);
            if (jobInfo == null) {
                jobInfo = new DBCronJobInfo();
                jobInfo.setCronjobName(cronJobName);
                jobInfo.setMasterServerId(serverId); 
            }

            if (!serverId.equals(jobInfo.getMasterServerId())) {
                return false; 
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


    public void unlockJob(String cronJobName) {
        try {
            DBCronJobInfo jobInfo = repository.findByCronjobName(cronJobName);
            if (jobInfo != null) {
                jobInfo.setIsLocked(false);
                jobInfo.setLastRunDateTime(LocalDateTime.now());
                repository.save(jobInfo);
            }

        } catch (

        ObjectOptimisticLockingFailureException e) {
            log.info("CronJob " + cronJobName + " could not be unlock : " + e.getMessage());
            return;
        } catch (StaleStateException e) {
            log.info("CronJob " + cronJobName + " could not be unlock : " + e.getMessage());
            return;
        } catch (DataIntegrityViolationException e) {
            log.info("CronJob " + cronJobName + " could not be unlock : " + e.getMessage());
            return;
        }
    }


    public String getServerHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "UnknownHost";
        }
    }


}
