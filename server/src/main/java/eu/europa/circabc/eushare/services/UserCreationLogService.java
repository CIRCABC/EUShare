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


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import eu.europa.circabc.eushare.storage.repository.UserCreationLogRepository;
import eu.europa.circabc.eushare.storage.entity.DBUserCreationLog;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Optional;

@Service
public class UserCreationLogService {

    private static final int USER_CREATION_THRESHOLD = 10;


    private final UserCreationLogRepository repository;

    @Autowired
    public UserCreationLogService(UserCreationLogRepository repository) {
        this.repository = repository;
    }

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
        // Calculate the date for the previous day
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        
        // Convert java.util.Date to java.sql.Date
        java.sql.Date yesterday = new java.sql.Date(calendar.getTimeInMillis());
    
        Optional<DBUserCreationLog> optionalLog = repository.findByDateCreated(yesterday);
        
        if (optionalLog.isPresent()) {
            DBUserCreationLog log = optionalLog.get();
            if (log.getUserCount() > USER_CREATION_THRESHOLD) {
                // Alerte ! Trop de nouveaux utilisateurs aujourd'hui.
                // Ajoutez ici le code pour envoyer une alerte, un e-mail ou toute autre action que vous souhaitez effectuer.
            }
        }
    }
    

    public void removeOldLogs() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        Date thresholdDate = new Date(cal.getTimeInMillis());
        repository.deleteByDateCreatedBefore(thresholdDate);
    }

    // ... existing methods ...
}