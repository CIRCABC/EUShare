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

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import eu.europa.circabc.eushare.configuration.cronjob.CronJobLock;
import eu.europa.circabc.eushare.storage.entity.DBFileUploadRate;
import eu.europa.circabc.eushare.storage.entity.DBMonitoring;
import eu.europa.circabc.eushare.storage.entity.DBMonitoring.Status;
import eu.europa.circabc.eushare.storage.entity.DBTrustLog;
import eu.europa.circabc.eushare.storage.entity.DBUser;
import eu.europa.circabc.eushare.storage.entity.DBUser.Role;
import eu.europa.circabc.eushare.storage.repository.FileUploadRateRepository;
import eu.europa.circabc.eushare.storage.repository.MonitoringRepository;
import eu.europa.circabc.eushare.storage.repository.TrustLogRepository;
import eu.europa.circabc.eushare.storage.repository.UserRepository;

@Service
public class FileUploadRateService {

    @Value("${eushare.file_upload_rate.external_threshold_hour}")
    private int EXTERNAL_THRESHOLD_HOUR;

    @Value("${eushare.file_upload_rate.trusted_external_threshold_hour}")
    private int TRUSTED_EXTERNAL_THRESHOLD_HOUR;

    @Value("${eushare.file_upload_rate.external_threshold_day}")
    private int EXTERNAL_THRESHOLD_DAY;

    @Value("${eushare.file_upload_rate.trusted_external_threshold_day}")
    private int TRUSTED_EXTERNAL_THRESHOLD_DAY;

    @Value("${eushare.file_upload_rate.external_user_hourly_realtime}")
    private int EXTERNAL_USER_HOURLY_REALTIME;

    @Value("${eushare.file_upload_rate.trusted_external_user_hourly_realtime}")
    private int TRUSTED_EXTERNAL_USER_HOURLY_REALTIME;

    @Value("${eushare.file_upload_rate.uploads_to_be_trusted_threshold}")
    private int UPLOADS_TO_BE_TRUSTED_THRESHOLD;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public FileUploadRateRepository repository;

    @Autowired
    public MonitoringRepository monitoringRepository;

    @Autowired
    private TrustLogRepository trustLogRepository;

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
    @CronJobLock
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

    public boolean realTimeCheck(DBUser user) {

        if (!user.getRole().equals(Role.EXTERNAL) && !user.getRole().equals(Role.TRUSTED_EXTERNAL)) {
            return false;
        }

        LocalDateTime currentHour = LocalDateTime.now(ZoneId.systemDefault()).withMinute(0).withSecond(0).withNano(0);

        Optional<DBFileUploadRate> optionalLogEntry = repository.findByDateHourAndUser(currentHour, user);

        int userLimit = user.getRole().equals(Role.TRUSTED_EXTERNAL) ? TRUSTED_EXTERNAL_USER_HOURLY_REALTIME
                : EXTERNAL_USER_HOURLY_REALTIME;

        if (optionalLogEntry.isPresent()) {
            DBFileUploadRate logEntry = optionalLogEntry.get();
            if (logEntry.getUploadCount() >= userLimit) {
                return true;
            }
        }

        return false;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @CronJobLock
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
        String message = "A monitoring alert for too uploads (" + event.toString() + ") of user :" + user.getEmail()
                + "\" has been raised at :" + dbMonitoring.getDatetime()
                + ".  Please inform CIRCABC-Share administrators about it. (more details in CIRCABC-Share admin console)";

        try {
            emailService.sendNotification("DIGIT-CIRCABC-SUPPORT@ec.europa.eu", message);
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
    @CronJobLock
    public void upgradeExternalUsers() {
        int uploadsThreshold = UPLOADS_TO_BE_TRUSTED_THRESHOLD;
        List<DBUser> users = userRepository.findExternalUsersWithMoreThanUploadsNotMonitored(DBUser.Role.EXTERNAL,
                uploadsThreshold);
        for (DBUser user : users) {
            user.setRole(DBUser.Role.TRUSTED_EXTERNAL);
            userRepository.save(user);

            DBTrustLog dbTrustLog = new DBTrustLog();

            dbTrustLog.setTrustDate(OffsetDateTime.now());
            dbTrustLog.setTruster("SYSTEM PROCESS");
            dbTrustLog.setTrusted(user.getEmail());
            dbTrustLog.setOrigin(DBTrustLog.Origin.REQUEST);

            trustLogRepository.save(dbTrustLog);
        }
    }

}
