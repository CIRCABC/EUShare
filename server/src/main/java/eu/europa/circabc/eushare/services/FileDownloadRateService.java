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

import eu.europa.circabc.eushare.storage.entity.DBFile;
import eu.europa.circabc.eushare.storage.entity.DBFileDownloadRate;
import eu.europa.circabc.eushare.storage.repository.FileDownloadRateRepository;
import eu.europa.circabc.eushare.storage.repository.FileRepository;
import eu.europa.circabc.eushare.storage.repository.FileUploadRateRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FileDownloadRateService {

    @Autowired
    public FileDownloadRateRepository repository;
    

    private static final int HOURLY_THRESHOLD = 100;
    private static final int DAILY_THRESHOLD = 500;

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

    @Scheduled(cron = "0 0 * * * ?")  
    public void hourlyCheck() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<DBFileDownloadRate> downloadsLastHour = repository.findByDateHour(oneHourAgo);

        for (DBFileDownloadRate downloadRate : downloadsLastHour) {
            DBFile file = downloadRate.getFile();
            if (downloadRate.getDownloadCount() > HOURLY_THRESHOLD) {
                sendAlert(file);
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")  
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
                sendAlert(file);
            }
        }

        removeOldDownloadLogs();
    }

    private void sendAlert(DBFile file) {
        // Votre logique d'alerte ici, comme envoyer un e-mail, une notification, etc.
        System.out.println("Alerte! Le fichier " + file.getFilename() + " a été téléchargé plus que le seuil autorisé.");
    }

    private void removeOldDownloadLogs() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        repository.deleteByDateHourBefore(thirtyDaysAgo);
    }
}
