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

import java.time.LocalDateTime;

@Entity
@Table(name = "monitoring")
public class DBMonitoring {

    public enum Event {
        DOWNLOAD,
        UPLOAD,
        USER
    }

    @Id
    @Column(nullable = false)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @Column(name = "datetime", nullable = false)
    private LocalDateTime datetime;

    @Enumerated(EnumType.STRING)
    @Column(name = "event", nullable = false, length = 8) // maximum length of our longest enum value
    private Event event;

    @Column(name = "file_id", nullable = true)
    private String fileId;

    @Column(name = "user_id", nullable = true)
    private String userId;

    @Column(name = "counter", nullable = false)
    private long counter;

    public DBMonitoring() {
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

    

}
