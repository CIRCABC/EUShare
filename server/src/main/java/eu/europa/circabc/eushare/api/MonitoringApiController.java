/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import eu.europa.circabc.eushare.model.EnumConverter;
import eu.europa.circabc.eushare.model.Monitoring;
import eu.europa.circabc.eushare.model.MonitoringDetails;
import eu.europa.circabc.eushare.security.AdminOnly;
import eu.europa.circabc.eushare.security.CaptchaValidator;
import eu.europa.circabc.eushare.services.EmailService;
import eu.europa.circabc.eushare.services.FileService;
import eu.europa.circabc.eushare.services.UserService;
import eu.europa.circabc.eushare.storage.dto.MonitoringDetailsDTO;
import eu.europa.circabc.eushare.storage.entity.DBAbuse;
import eu.europa.circabc.eushare.storage.entity.DBMonitoring;
import eu.europa.circabc.eushare.storage.entity.DBUser;
import eu.europa.circabc.eushare.storage.repository.MonitoringRepository;
import eu.europa.circabc.eushare.storage.repository.UserRepository;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
public class MonitoringApiController implements MonitoringApi {

    private static final Map<String, String> MONITORING_EVENTS;

    static {
        MONITORING_EVENTS = new HashMap<>();
        MONITORING_EVENTS.put("download_rate_hour", "Too many downloads of your file (Hourly)");
        MONITORING_EVENTS.put("download_rate_day", "Too many downloads of your file (Daily)");
        MONITORING_EVENTS.put("upload_rate_hour", "Too many uploads (Hourly)");
        MONITORING_EVENTS.put("upload_rate_day", "Too many uploads (Daily)");
        MONITORING_EVENTS.put("user_creation_day", "Too many new users (Daily)");
        // Add more mappings as necessary
    }

    public static String getEventDescription(String key) {
        return MONITORING_EVENTS.get(key);
    }

    @Autowired
    private MonitoringRepository monitoringRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public UserService userService;

    @Autowired
    public FileService fileService;

    @Autowired
    private EmailService emailService;

    @AdminOnly
    @Override
    public ResponseEntity<Monitoring> getMonitoringEntry(String id) {

        DBMonitoring dbMonitoring = monitoringRepository.findOneById(id);
        if (dbMonitoring == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Monitoring report not found");
        }

        Monitoring monitoring = dbMonitoring.toMonitoring();
        return ResponseEntity.ok(monitoring);

    }

    @AdminOnly
    @Override
    public ResponseEntity<List<MonitoringDetails>> getMonitoringEntryList() {
        List<MonitoringDetailsDTO> monitoringDetailsDto = monitoringRepository.findAllDetails();
        List<MonitoringDetails> monitorings = new ArrayList<>();

        for (MonitoringDetailsDTO dto : monitoringDetailsDto) {
            MonitoringDetails detail = dto.toMonitoringDetails();
            monitorings.add(detail);
        }

        return ResponseEntity.ok(monitorings);
    }

    @AdminOnly
    @Override
    public ResponseEntity<Void> deleteMonitoringEntry(@PathVariable("id") String id) {
        DBMonitoring dbMonitoring = monitoringRepository.findOneById(id);

        monitoringRepository.delete(dbMonitoring);

        return ResponseEntity.ok().build();
    }

    @AdminOnly
    @Override
    public ResponseEntity<MonitoringDetails> updateMonitoringEntry(@PathVariable("id") String id,
            @Valid @RequestBody MonitoringDetails monitoringDetails) {

         DBMonitoring dbMonitoring = monitoringRepository.findOneById(monitoringDetails.getID());
         DBMonitoring.Status dbMonitoringStatus = EnumConverter.convert(monitoringDetails.getStatus(),DBMonitoring.Status.class);
         dbMonitoring.setStatus(dbMonitoringStatus);
         monitoringRepository.save(dbMonitoring);      

        if (monitoringDetails.getStatus() == MonitoringDetails.StatusEnum.APPROVED) {

            String userId = monitoringDetails.getUserId();
            if (userId != null) {

                DBUser user = userRepository.findOneById(userId);
                if (user != null) {
                    user.setStatus(DBUser.Status.valueOf(monitoringDetails.getUploaderStatus()));
                    userRepository.save(user);
                    if (monitoringDetails.getFileId() != null) {
                        fileService.freezeFile(monitoringDetails.getFileId());
                    }
                    else {
                        //fileService.freezeFilesFromUser(userId, userId);
                    }
                }
            }
            
              try {
              this.emailService.sendMonitoringBlameNotification("DIGIT-CIRCABC-SUPPORT@ec.europa.eu", monitoringDetails);
             } catch (MessagingException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
              }
             


        }
        if (monitoringDetails.getStatus() == MonitoringDetails.StatusEnum.DENIED) {
            String uploader = monitoringDetails.getUploaderEmail();
            if (uploader != null) {
                DBUser user = userRepository.findOneByEmailIgnoreCase(uploader);
                if (user != null) {
                    user.setStatus(DBUser.Status.REGULAR);
                    userRepository.save(user);
                    if (monitoringDetails.getFileId() != null) {
                        fileService.unfreezeFile(monitoringDetails.getFileId());
                    }
                }
            }
        }

        return ResponseEntity.ok(monitoringDetails);
    }
}
