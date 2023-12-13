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

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

        boolean lockAcquired = false;
        try {
            lockAcquired = cronJobLockService.lockJob(cronJobName, cronExpression);
            if (lockAcquired) {
                return joinPoint.proceed();
            } else {
                return null;
            }
        } finally {
            if (lockAcquired) {
                Thread.sleep(10000);
                cronJobLockService.unlockJob(cronJobName);
            }
        }

    }
}
