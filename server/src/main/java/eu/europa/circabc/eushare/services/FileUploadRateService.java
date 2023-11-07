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
import eu.europa.circabc.eushare.storage.repository.MonitoringRepository;
import eu.europa.circabc.eushare.storage.repository.UserRepository;
import eu.europa.circabc.eushare.storage.entity.DBFile;
import eu.europa.circabc.eushare.storage.entity.DBFileUploadRate;
import eu.europa.circabc.eushare.storage.entity.DBMonitoring;
import eu.europa.circabc.eushare.storage.entity.DBMonitoring.Status;
import eu.europa.circabc.eushare.storage.entity.DBUser;
import eu.europa.circabc.eushare.storage.entity.DBUser.Role;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.mail.MessagingException;

@Service
public class FileUploadRateService {

   
    private static final int EXTERNAL_THRESHOLD_HOUR = 5;
    private static final int TRUSTED_EXTERNAL_THRESHOLD_HOUR = 10;
    private static final int EXTERNAL_THRESHOLD_DAY = 50;
    private static final int TRUSTED_EXTERNAL_THRESHOLD_DAY = 100;

    private static final int TRUSTED_UPLOADS_THRESHOLD = 50;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public FileUploadRateRepository repository;

    @Autowired
    public MonitoringRepository monitoringRepository;  

    @Autowired
    public EmailService emailService;

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

    @Scheduled(cron = "0 0 * * * ?") 
    public void hourlyCheck() {
        LocalDateTime currentHour = LocalDateTime.now(ZoneId.systemDefault()).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime oneHourAgo = currentHour.minusHours(1);
        List<DBFileUploadRate> uploadsLastHour = repository.findByDateHour(oneHourAgo);

        for (DBFileUploadRate uploadRate : uploadsLastHour) {
            DBUser user = uploadRate.getUser();
            Role userRole = user.getRole();
            int threshold = getThresholdForRole(userRole);
            if (uploadRate.getUploadCount() > threshold)
            saveMonitoringAndSendAlert(user, uploadRate.getUploadCount(), DBMonitoring.Event.UPLOAD_RATE_HOUR);

        }
    }

    @Scheduled(cron = "0 0 0 * * ?") 
    public void dailyCheck() {
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusDays(1);
        List<DBFileUploadRate> uploadsLastDay = repository.findByDateHourAfter(twentyFourHoursAgo);

        Map<DBUser, Integer> userUploadCounts = new HashMap<>();

        for (DBFileUploadRate uploadRate : uploadsLastDay) {
            userUploadCounts.put(uploadRate.getUser(),
                    userUploadCounts.getOrDefault(uploadRate.getUser(), 0) + uploadRate.getUploadCount());
        }

        for (Map.Entry<DBUser, Integer> entry : userUploadCounts.entrySet()) {
            DBUser user = entry.getKey();
            int count = entry.getValue();
            int threshold = getDailyThresholdForRole(user.getRole());
            if (count > threshold) {
                saveMonitoringAndSendAlert(user, count, DBMonitoring.Event.UPLOAD_RATE_DAY);
            }
        }

        removeOldUploadLogs();
    }

     private void saveMonitoringAndSendAlert(DBUser user, int count, DBMonitoring.Event event) {
        DBMonitoring dbMonitoring = new DBMonitoring();
        dbMonitoring.setStatus(Status.WAITING);
        dbMonitoring.setCounter(count);
        dbMonitoring.setEvent(event);
        dbMonitoring.setDatetime(LocalDateTime.now());
      
        dbMonitoring.setUserId(user.getId());
        monitoringRepository.save(dbMonitoring);
               String message = "A monitoring alert for too uploads ("+event.toString()+ ") of user :" + user.getEmail() + "\" has been raised at :"+ dbMonitoring.getDatetime() + ".  Please inform CIRCABC-Share administrators about it. (more details in CIRCABC-Share admin console)";
        
        try {
            emailService.sendNotification("DIGIT-CIRCABC-SUPPORT@ec.europa.eu",message);
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
            default:
                return Integer.MAX_VALUE; 
        }
    }

    private int getDailyThresholdForRole(Role role) {
        switch (role) {
            case EXTERNAL:
                return EXTERNAL_THRESHOLD_DAY;
            case TRUSTED_EXTERNAL:
                return TRUSTED_EXTERNAL_THRESHOLD_DAY;
            default:
                return Integer.MAX_VALUE; 
        }
    }


    @Scheduled(cron = "0 0 * * * ?")
    public void upgradeExternalUsers() {
      int uploadsThreshold = TRUSTED_UPLOADS_THRESHOLD; 
      List<DBUser> users = userRepository.findExternalUsersWithMoreThanUploadsNotMonitored(DBUser.Role.EXTERNAL, uploadsThreshold);
      for (DBUser user : users) {
        user.setRole(DBUser.Role.TRUSTED_EXTERNAL);
        userRepository.save(user);
      }
    }
 
}
