/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.storage.entity;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import eu.europa.circabc.eushare.model.Monitoring;
import eu.europa.circabc.eushare.storage.dto.MonitoringDetailsDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NamedNativeQuery(name = "MonitoringDetailsDTO.findAllDetails", query = "SELECT " +
        "m.id AS id, " +
        "m.datetime AS datetime, " +
        "m.event AS event, " +
        "m.file_id AS file_id, " +
        "m.user_id AS user_id, " +
        "m.counter AS counter, " +
        "m.status AS status, " +

        "CASE " +
        "WHEN m.event IN ('DOWNLOAD_RATE_HOUR', 'DOWNLOAD_RATE_DAY') THEN f.filename " +
        "ELSE NULL " +
        "END AS filename, " +

        "CASE " +
        "WHEN m.event IN ('DOWNLOAD_RATE_HOUR', 'DOWNLOAD_RATE_DAY') THEN f.file_size " +
        "ELSE NULL " +
        "END AS filesize, " +

        "CASE " +
        "WHEN m.event IN ('DOWNLOAD_RATE_HOUR', 'DOWNLOAD_RATE_DAY', 'UPLOAD_RATE_HOUR', 'UPLOAD_RATE_DAY') THEN u.email "
        +
        "ELSE NULL " +
        "END AS uploader_email, " +

        "CASE " +
        "WHEN m.event IN ('DOWNLOAD_RATE_HOUR', 'DOWNLOAD_RATE_DAY', 'UPLOAD_RATE_HOUR', 'UPLOAD_RATE_DAY') THEN u.name "
        +
        "ELSE NULL " +
        "END AS uploader_name, " +

        "CASE " +
        "WHEN m.event IN ('DOWNLOAD_RATE_HOUR', 'DOWNLOAD_RATE_DAY', 'UPLOAD_RATE_HOUR', 'UPLOAD_RATE_DAY') THEN u.status "
        +
        "ELSE NULL " +
        "END AS uploader_status," +

        "CASE " +
        "WHEN m.event = 'USER_CREATION_DAY' THEN (SELECT GROUP_CONCAT(u.email) FROM users u WHERE u.creation_date >= m.datetime - INTERVAL 24 HOUR) "
        +
        "WHEN m.event = 'UPLOAD_RATE_HOUR' THEN (SELECT GROUP_CONCAT(f.filename) FROM files f WHERE f.uploader_id = m.user_id AND f.created >= m.datetime - INTERVAL 2 HOUR) "
        +
        "WHEN m.event = 'UPLOAD_RATE_DAY' THEN (SELECT GROUP_CONCAT(f.filename) FROM files f WHERE f.uploader_id = m.user_id AND f.created >= m.datetime - INTERVAL 24 HOUR) "
        +
        "ELSE NULL " +
        "END AS description " +

        "FROM " +
        "monitoring m " +
        "LEFT JOIN files f ON (m.event IN ('DOWNLOAD_RATE_HOUR', 'DOWNLOAD_RATE_DAY') AND m.file_id = f.FILE_ID) " +
        "LEFT JOIN users u ON (m.event IN ('DOWNLOAD_RATE_HOUR', 'DOWNLOAD_RATE_DAY') AND f.uploader_id = u.id) " +
        "OR (m.event IN ('UPLOAD_RATE_HOUR', 'UPLOAD_RATE_DAY') AND m.user_id = u.id) ORDER BY datetime DESC", resultSetMapping = "MonitoringDetailsDTOMapping")

@SqlResultSetMapping(name = "MonitoringDetailsDTOMapping", classes = @ConstructorResult(targetClass = MonitoringDetailsDTO.class, columns = {
        @ColumnResult(name = "id"),
        @ColumnResult(name = "datetime", type = LocalDateTime.class),
        @ColumnResult(name = "event", type = String.class),
        @ColumnResult(name = "file_id"),
        @ColumnResult(name = "user_id"),
        @ColumnResult(name = "counter", type = Long.class),
        @ColumnResult(name = "status", type = String.class),
        @ColumnResult(name = "filename"),
        @ColumnResult(name = "filesize", type = BigDecimal.class),
        @ColumnResult(name = "uploader_email"),
        @ColumnResult(name = "uploader_name"),
        @ColumnResult(name = "uploader_status", type = String.class),
        @ColumnResult(name = "description", type = String.class)
}))

@Table(name = "monitoring")
public class DBMonitoring {

    @Id
    @Column(nullable = false)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @Column(name = "datetime", nullable = false)
    private LocalDateTime datetime;

    @Enumerated(EnumType.STRING)
    @Column(name = "event", nullable = false)
    private Event event;

    public enum Event {
        DOWNLOAD_RATE_HOUR,
        UPLOAD_RATE_HOUR,
        DOWNLOAD_RATE_DAY,
        UPLOAD_RATE_DAY,
        USER_CREATION_DAY, 
        BRUTE_FORCE_ATTACK_DAY, 
        BRUTE_FORCE_ATTACK_HOUR
    }

    @Column(name = "file_id", nullable = true)
    private String fileId;

    @Column(name = "user_id", nullable = true)
    private String userId;

    @Column(name = "counter", nullable = false)
    private long counter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public enum Status {
        WAITING,
        APPROVED,
        DENIED
    }

    public DBMonitoring() {
    }

    public Monitoring toMonitoring() {
        Monitoring monitoring = new Monitoring();
        monitoring.setID(this.getId());
        monitoring.setDatetime(this.getDatetime().toString());
        monitoring.setEvent(Monitoring.EventEnum.valueOf(this.getEvent().name()));
        monitoring.setFileId(this.getFileId());
        monitoring.setUserId(this.getUserId());
        monitoring.setCounter(this.getCounter());
        monitoring.setStatus(Monitoring.StatusEnum.valueOf(this.getStatus().name()));
        return monitoring;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
