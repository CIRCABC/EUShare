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

import java.time.LocalDateTime;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "FileDownloadRate")
public class DBFileDownloadRate {

    @Id
    @Column(nullable = false)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fileID", referencedColumnName = "FILE_ID", nullable = false)
    private DBFile file;

    @Column(name = "dateHour", nullable = false)
    private LocalDateTime dateHour;

    @Column(name = "downloadCount", nullable = false, columnDefinition = "INT DEFAULT 1")
    private int downloadCount;



    public DBFile getFile() {
        return file;
    }

    public void setFile(DBFile file) {
        this.file = file;
    }

    public LocalDateTime getDateHour() {
        return dateHour;
    }

    public void setDateHour(LocalDateTime dateHour) {
        this.dateHour = dateHour;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public String getId() {
        return id;
    }

    
}
