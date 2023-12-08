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
import eu.europa.circabc.eushare.storage.entity.DBFile;
import eu.europa.circabc.eushare.storage.entity.DBFileDownloadRate;
import eu.europa.circabc.eushare.storage.entity.DBMonitoring;
import eu.europa.circabc.eushare.storage.entity.DBMonitoring.Status;
import eu.europa.circabc.eushare.storage.repository.FileDownloadRateRepository;
import eu.europa.circabc.eushare.storage.repository.MonitoringRepository;

@Service
public class FileDownloadRateService {

    @Autowired
    public FileDownloadRateRepository repository;

    @Autowired
    public MonitoringRepository monitoringRepository;

    @Autowired
    public EmailService emailService;

    @Value("${eushare.file_download_rate.hourly_threshold}")
    private int HOURLY_THRESHOLD;

    @Value("${eushare.file_download_rate.hourly_threshold_rt}")
    private int HOURLY_THRESHOLD_RT;

    @Value("${eushare.file_download_rate.daily_threshold}")
    private int DAILY_THRESHOLD;

    public void logFileDownload(DBFile file) {
        LocalDateTime currentHour = LocalDateTime.now(ZoneId.systemDefault()).withMinute(0).withSecond(0).withNano(0);

        Optional<DBFileDownloadRate> optionalLogEntry = repository.findByDateHourAndFile(currentHour, file);

        if (optionalLogEntry.isPresent()) {
            DBFileDownloadRate logEntry = optionalLogEntry.get();
            logEntry.setDownloadCount(logEntry.getDownloadCount() + 1);
            repository.save(logEntry);
        } else {
            DBFileDownloadRate newLogEntry = new DBFileDownloadRate();
            newLogEntry.setDateHour(currentHour);
            newLogEntry.setFile(file);
            newLogEntry.setDownloadCount(1);
            repository.save(newLogEntry);
        }
    }

    public boolean realTimeCheck(DBFile file) {
        LocalDateTime currentHour = LocalDateTime.now(ZoneId.systemDefault()).withMinute(0).withSecond(0).withNano(0);

        Optional<DBFileDownloadRate> optionalLogEntry = repository.findByDateHourAndFile(currentHour, file);

        if (optionalLogEntry.isPresent()) {
            DBFileDownloadRate logEntry = optionalLogEntry.get();
            if (logEntry.getDownloadCount() > HOURLY_THRESHOLD_RT) {

                return true;

            }
        }
        return false;
    }

    @Scheduled(cron = "0 0 * * * ?")
    @CronJobLock
    public void hourlyCheck() {
        LocalDateTime currentHour = LocalDateTime.now(ZoneId.systemDefault()).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime oneHourAgo = currentHour.minusHours(1);

        List<DBFileDownloadRate> downloadsLastHour = repository.findByDateHour(oneHourAgo);

        for (DBFileDownloadRate downloadRate : downloadsLastHour) {
            DBFile file = downloadRate.getFile();

            if (downloadRate.getDownloadCount() > HOURLY_THRESHOLD) {

                saveMonitoringAndSendAlert(file, downloadRate.getDownloadCount(),
                        DBMonitoring.Event.DOWNLOAD_RATE_HOUR);

            }
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @CronJobLock
    public void dailyCheck() {
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusDays(1);
        List<DBFileDownloadRate> downloadsLastDay = repository.findByDateHourAfter(twentyFourHoursAgo);

        Map<DBFile, Integer> fileDownloadCounts = new HashMap<>();

        for (DBFileDownloadRate downloadRate : downloadsLastDay) {
            fileDownloadCounts.put(downloadRate.getFile(),
                    fileDownloadCounts.getOrDefault(downloadRate.getFile(), 0) + downloadRate.getDownloadCount());
        }

        for (Map.Entry<DBFile, Integer> entry : fileDownloadCounts.entrySet()) {
            DBFile file = entry.getKey();
            int count = entry.getValue();
            if (count > DAILY_THRESHOLD) {
                saveMonitoringAndSendAlert(file, count, DBMonitoring.Event.DOWNLOAD_RATE_DAY);
            }
        }

        removeOldDownloadLogs();
    }

    private void saveMonitoringAndSendAlert(DBFile file, int count, DBMonitoring.Event event) {
        DBMonitoring dbMonitoring = new DBMonitoring();
        dbMonitoring.setStatus(Status.WAITING);
        dbMonitoring.setCounter(count);
        dbMonitoring.setEvent(event);
        dbMonitoring.setDatetime(LocalDateTime.now());
        dbMonitoring.setFileId(file.getId());
        dbMonitoring.setUserId(file.getUploader().getId());
        monitoringRepository.save(dbMonitoring);

        String message = "A monitoring alert for too many downloads (" + event.toString() + ") of file :"
                + file.getFilename() + "\" has been raised at :" + dbMonitoring.getDatetime()
                + ".  Please inform CIRCABC-Share administrators about it. (more details in CIRCABC-Share admin console)";

        try {
            emailService.sendNotification("DIGIT-CIRCABC-SUPPORT@ec.europa.eu", message);
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void removeOldDownloadLogs() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        repository.deleteByDateHourBefore(thirtyDaysAgo);
    }
}
