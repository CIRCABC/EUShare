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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import eu.europa.circabc.eushare.storage.entity.DBCronJobInfo;
import eu.europa.circabc.eushare.storage.repository.CronJobInfoRepository;

@Service
public class CronJobLockService {

    @Autowired
    private CronJobInfoRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void lockJob(String cronJobName, String cronExpression) {
        DBCronJobInfo jobInfo = repository.findByCronjobName(cronJobName);
        if (jobInfo == null) {
            jobInfo = new DBCronJobInfo();
            jobInfo.setCronjobName(cronJobName);
        }
        jobInfo.setIsLocked(true);
        jobInfo.setCronjobDelay(cronExpression);
        repository.save(jobInfo);
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

    public boolean isEligibleToRun(String cronJobName, String cronExpression) {
        DBCronJobInfo jobInfo = repository.findByCronjobName(cronJobName);
        if (jobInfo == null)
            return true;

        boolean isLocked = jobInfo.getIsLocked();
        LocalDateTime lastRun = jobInfo.getLastRunDateTime();
        long timeUntilNextExecution = calculateTimeUntilNextExecutionInMinutes(cronExpression);

        if (lastRun == null)
            return true;

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime nextScheduledRun = lastRun.plusMinutes(timeUntilNextExecution);

        return !isLocked || currentTime.isAfter(nextScheduledRun);
    }

    private long calculateTimeUntilNextExecutionInMinutes(String cronExpression) {
        CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ));
        Cron quartzCron = parser.parse(cronExpression);
        ExecutionTime executionTime = ExecutionTime.forCron(quartzCron);

        Optional<ZonedDateTime> nextExecution = executionTime.nextExecution(ZonedDateTime.now());
        if (nextExecution.isPresent()) {
            ZonedDateTime nextExecTime = nextExecution.get();
            return Duration.between(ZonedDateTime.now(), nextExecTime).toMinutes();
        } else {
            return 0L; // If next execution time is not available
        }
    }
}
