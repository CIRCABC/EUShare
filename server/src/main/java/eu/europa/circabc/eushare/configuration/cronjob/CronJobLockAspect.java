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

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import eu.europa.circabc.eushare.storage.entity.DBCronJobInfo;
import eu.europa.circabc.eushare.storage.repository.CronJobInfoRepository;

import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Aspect
@Component
public class CronJobLockAspect {

    @Autowired
    private CronJobInfoRepository repository;

    @Around("@annotation(CronJobLock) && @annotation(scheduled)")
    public Object aroundScheduledTasks(ProceedingJoinPoint joinPoint, Scheduled scheduled) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = method.getName();
        String className = method.getDeclaringClass().getSimpleName();
        String cronJobName = className + "." + methodName;
        String cronExpression = scheduled.cron();

        DBCronJobInfo jobInfo = repository.findByCronjobName(cronJobName);
        if (jobInfo == null) {
            jobInfo = new DBCronJobInfo();
            jobInfo.setIsLocked(false);
            jobInfo.setCronjobDelay(cronExpression);
            jobInfo.setCronjobName(cronJobName);
        }

        if (isEligibleToRun(jobInfo, cronExpression)) {
            jobInfo.setIsLocked(true);
            repository.save(jobInfo);
            Object result = joinPoint.proceed(); 
            Thread.sleep(2000); 
            jobInfo.setIsLocked(false);
            jobInfo.setLastRunDateTime(LocalDateTime.now());
            repository.save(jobInfo);
            return result;
        } else {
            return null; 
        }
    }

    private boolean isEligibleToRun(DBCronJobInfo jobInfo, String cronExpression) {

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
