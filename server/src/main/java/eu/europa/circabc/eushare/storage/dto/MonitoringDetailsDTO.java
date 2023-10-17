/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */

package eu.europa.circabc.eushare.storage.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import eu.europa.circabc.eushare.model.EnumConverter;
import eu.europa.circabc.eushare.model.MonitoringDetails;
import eu.europa.circabc.eushare.storage.entity.DBMonitoring.Event;
import eu.europa.circabc.eushare.storage.entity.DBMonitoring.Status;

public class MonitoringDetailsDTO {
    private static final long serialVersionUID = 1L;

    private String id;
    private LocalDateTime datetime;
    @Enumerated(value = EnumType.STRING)
    private Event event;
    private String fileId;
    private String userId;
    private long counter;
    private String filename;
    private BigDecimal filesize;
    private String uploaderEmail;
    private String uploaderName;
    private String uploaderStatus;
    private String description;
    private Status status;

    private MonitoringDetailsDTO() {
    }

    public MonitoringDetailsDTO(String id, LocalDateTime datetime, String eventStr, String fileId, String userId,
            long counter, String status, String filename, BigDecimal filesize,
            String uploaderEmail, String uploaderName, String uploaderStatus, String description) {
        this.id = id;
        this.datetime = datetime;
        this.event = Event.valueOf(eventStr);
        this.fileId = fileId;
        this.userId = userId;
        this.counter = counter;
        this.status = Status.valueOf(status); 
        this.filename = filename;
        this.filesize = filesize;
        this.uploaderEmail = uploaderEmail;
        this.uploaderName = uploaderName;
        this.uploaderStatus = uploaderStatus;
        this.description = description;
    }

    public MonitoringDetails toMonitoringDetails() {
        MonitoringDetails monitoringDetails = new MonitoringDetails();
        monitoringDetails.setID(this.id);
        monitoringDetails.setDatetime(this.datetime.toString());

        MonitoringDetails.EventEnum monitoringDetailsEvent = EnumConverter.convert(this.event,
                MonitoringDetails.EventEnum.class);
        monitoringDetails.setEvent(monitoringDetailsEvent);

        monitoringDetails.setFileId(this.fileId);
        monitoringDetails.setUserId(this.userId);
        monitoringDetails.setCounter(this.counter);

        MonitoringDetails.StatusEnum monitoringDetailsStatus = EnumConverter.convert(this.status,
        MonitoringDetails.StatusEnum.class);
        monitoringDetails.setStatus(monitoringDetailsStatus);

        monitoringDetails.setFilename(this.filename);
        monitoringDetails.setFilesize(this.filesize);
        monitoringDetails.setUploaderEmail(this.uploaderEmail);
        monitoringDetails.setUploaderName(this.uploaderName);
        monitoringDetails.setUploaderStatus(this.uploaderStatus);
        monitoringDetails.setDescription(this.description);
        return monitoringDetails;
    }
}
