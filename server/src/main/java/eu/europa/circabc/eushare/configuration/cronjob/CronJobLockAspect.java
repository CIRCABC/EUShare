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

import javax.transaction.Transactional;

@Aspect
@Component
public class CronJobLockAspect {

    @Autowired
    private CronJobLockService cronJobLockService;

    @Around("@annotation(CronJobLock) && @annotation(scheduled)")
    public Object aroundScheduledTasks(ProceedingJoinPoint joinPoint, Scheduled scheduled) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = method.getName();
        String className = method.getDeclaringClass().getSimpleName();
        String cronJobName = className + "." + methodName;
        String cronExpression = scheduled.cron();

        if (cronJobLockService.isEligibleToRun(cronJobName, cronExpression)) {
            try {
                cronJobLockService.lockJob(cronJobName, cronExpression);
                return joinPoint.proceed();
            } finally {
                cronJobLockService.unlockJob(cronJobName);
            }
        } else {
            return null; 
        }
    }
}




