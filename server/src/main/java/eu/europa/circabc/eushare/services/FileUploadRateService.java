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
import eu.europa.circabc.eushare.storage.repository.FileUploadRateRepository;
import eu.europa.circabc.eushare.storage.repository.UserRepository;
import eu.europa.circabc.eushare.storage.entity.DBFileUploadRate;
import eu.europa.circabc.eushare.storage.entity.DBUser;
import eu.europa.circabc.eushare.storage.entity.DBUser.Role;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FileUploadRateService {

   
    private static final int EXTERNAL_THRESHOLD_HOUR = 10;
    private static final int TRUSTED_EXTERNAL_THRESHOLD_HOUR = 50;
    private static final int EXTERNAL_THRESHOLD_DAY = 50;
    private static final int TRUSTED_EXTERNAL_THRESHOLD_DAY = 300;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public FileUploadRateRepository repository;
    

    public void logFileUpload(String userID) {
        LocalDateTime currentHour = LocalDateTime.now(ZoneId.systemDefault()).withMinute(0).withSecond(0).withNano(0);

        DBUser currentUser = userRepository.findOneById(userID);

        Optional<DBFileUploadRate> optionalLogEntry = repository.findByDateHourAndUser(currentHour, currentUser);

        if (optionalLogEntry.isPresent()) {
            DBFileUploadRate logEntry = optionalLogEntry.get();
            logEntry.setUploadCount(logEntry.getUploadCount() + 1);
            repository.save(logEntry);
        } else {

            DBFileUploadRate newLogEntry = new DBFileUploadRate();
            newLogEntry.setDateHour(currentHour);
            newLogEntry.setUser(currentUser);
            newLogEntry.setUploadCount(1);
            repository.save(newLogEntry);
        }
    }

    @Scheduled(cron = "0 0 * * * ?") // Chaque heure
    public void hourlyCheck() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<DBFileUploadRate> uploadsLastHour = repository.findByDateHour(oneHourAgo);

        for (DBFileUploadRate uploadRate : uploadsLastHour) {
            DBUser user = uploadRate.getUser();
            Role userRole = user.getRole();
            int threshold = getThresholdForRole(userRole);
            if (uploadRate.getUploadCount() > threshold)
                sendAlert(user);
        }
    }

    @Scheduled(cron = "0 0 0 * * ?") // Every day at midnight
    public void dailyCheck() {
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusDays(1);
        List<DBFileUploadRate> uploadsLastDay = repository.findByDateHourAfter(twentyFourHoursAgo);

        Map<DBUser, Integer> userUploadCounts = new HashMap<>();

        // Sum the uploads for each user in the last 24 hours
        for (DBFileUploadRate uploadRate : uploadsLastDay) {
            userUploadCounts.put(uploadRate.getUser(),
                    userUploadCounts.getOrDefault(uploadRate.getUser(), 0) + uploadRate.getUploadCount());
        }

        // Check if the summed upload counts exceed the daily threshold for each user
        // role
        for (Map.Entry<DBUser, Integer> entry : userUploadCounts.entrySet()) {
            DBUser user = entry.getKey();
            int count = entry.getValue();
            int threshold = getDailyThresholdForRole(user.getRole());
            if (count > threshold) {
                sendAlert(user);
            }
        }

        // Effacement des logs vieux de plus de 30 jours
        removeOldUploadLogs();
    }

    public void removeOldUploadLogs() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        repository.deleteByDateHourBefore(thirtyDaysAgo);
    }

    private int getThresholdForRole(Role role) {
        switch (role) {
            case EXTERNAL:
                return EXTERNAL_THRESHOLD_HOUR;
            case TRUSTED_EXTERNAL:
                return TRUSTED_EXTERNAL_THRESHOLD_HOUR;
            // ... other cases
            default:
                return -1; // or some default value
        }
    }

    private int getDailyThresholdForRole(Role role) {
        switch (role) {
            case EXTERNAL:
                return EXTERNAL_THRESHOLD_DAY;
            case TRUSTED_EXTERNAL:
                return TRUSTED_EXTERNAL_THRESHOLD_DAY;
            default:
                return Integer.MAX_VALUE; // No threshold for other roles
        }
    }

    private void sendAlert(DBUser user) {
        // Logic to send alert to the user or admins
    }

}
