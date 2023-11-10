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

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Optional;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import eu.europa.circabc.eushare.storage.entity.DBMonitoring;
import eu.europa.circabc.eushare.storage.entity.DBMonitoring.Status;
import eu.europa.circabc.eushare.storage.entity.DBUserCreationLog;
import eu.europa.circabc.eushare.storage.repository.MonitoringRepository;
import eu.europa.circabc.eushare.storage.repository.UserCreationLogRepository;

@Service
public class UserCreationLogService {

    @Value("${eushare.user_creation_log.user_creation_threshold}")
    private int USER_CREATION_THRESHOLD;

    @Autowired
    private UserCreationLogRepository repository;

    @Autowired
    public MonitoringRepository monitoringRepository;

    @Autowired
    public EmailService emailService;

    public void logNewUserCreation() {
        Date today = Date.valueOf(LocalDate.now());

        Optional<DBUserCreationLog> optionalLogEntry = repository.findByDateCreated(today);

        if (optionalLogEntry.isPresent()) {
            DBUserCreationLog logEntry = optionalLogEntry.get();
            logEntry.setUserCount(logEntry.getUserCount() + 1);
            repository.save(logEntry);
        } else {
            DBUserCreationLog newLogEntry = new DBUserCreationLog();
            newLogEntry.setDateCreated(today);
            newLogEntry.setUserCount(1);
            repository.save(newLogEntry);
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void dailyCheck() {
        checkUserCreationThreshold();
        removeOldLogs();
    }

    public void checkUserCreationThreshold() {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);

        java.sql.Date yesterday = new java.sql.Date(calendar.getTimeInMillis());

        Optional<DBUserCreationLog> optionalLog = repository.findByDateCreated(yesterday);

        if (optionalLog.isPresent()) {
            DBUserCreationLog log = optionalLog.get();
            if (log.getUserCount() > USER_CREATION_THRESHOLD) {
                DBMonitoring dbMonitoring = new DBMonitoring();
                dbMonitoring.setStatus(Status.WAITING);
                dbMonitoring.setCounter(log.getUserCount());
                dbMonitoring.setEvent(DBMonitoring.Event.USER_CREATION_DAY);
                dbMonitoring.setDatetime(LocalDateTime.now());

                monitoringRepository.save(dbMonitoring);

                String message = "A monitoring alert for abnormal number of new users (" + log.getUserCount() +
                        ") in the last 24h has been raised at :" + dbMonitoring.getDatetime() +
                        ".  Please inform CIRCABC-Share administrators about it. (more details in CIRCABC-Share admin console)";

                try {
                    emailService.sendNotification("DIGIT-CIRCABC-SUPPORT@ec.europa.eu", message);
                } catch (MessagingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void removeOldLogs() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        Date thresholdDate = new Date(cal.getTimeInMillis());
        repository.deleteByDateCreatedBefore(thresholdDate);
    }

}