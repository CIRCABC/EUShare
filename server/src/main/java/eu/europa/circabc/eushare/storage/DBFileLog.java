/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.storage;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eu.europa.circabc.eushare.model.FileLog;

@Entity
@Table(name = "logs")
public class DBFileLog {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "LOG_ID", nullable = false, unique = true)
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "Logs_to_file"))
    private DBFile file;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false, length = 10)
    private String downloadLink;

    @Column(nullable = false)
    private LocalDateTime downloadDate;

    @SuppressWarnings("java:S107")
    public DBFileLog(DBFile file, String recipient,
            LocalDateTime downloadDate, String downloadLink) {
        this.file = file;
        this.recipient = recipient;
        this.downloadDate = downloadDate;
        this.downloadLink = downloadLink;
    }

    private DBFileLog() {
    }

    public FileLog toFileLog() {
        FileLog fileLog = new FileLog();
        fileLog.setFileId(file.getId());
        final ZoneId zone = ZoneId.of("Europe/Paris");
       
        ZoneOffset zoneOffSet = zone.getRules().getOffset(downloadDate);
        OffsetDateTime offsetDateTime = downloadDate.atOffset(zoneOffSet);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        fileLog.setDownloadDate(offsetDateTime.format(fmt));
        fileLog.setDownloadLink(downloadLink);
        fileLog.setRecipient(recipient);
        return fileLog;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((downloadDate == null) ? 0 : downloadDate.hashCode());
        result = prime * result + ((downloadLink == null) ? 0 : downloadLink.hashCode());
        result = prime * result + ((file == null) ? 0 : file.hashCode());
        result = prime * result + ((recipient == null) ? 0 : recipient.hashCode());

        return result;
    }

    @Override
    // NOSONAR
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DBFileLog other = (DBFileLog) obj;
        if (downloadDate == null) {
            if (other.downloadDate != null)
                return false;
        } else if (!downloadDate.equals(other.downloadDate))
            return false;
        if (downloadLink == null) {
            if (other.downloadLink != null)
                return false;
        } else if (!downloadLink.equals(other.downloadLink))
            return false;
        if (file == null) {
            if (other.file != null)
                return false;
        } else if (!file.equals(other.file))
            return false;
        if (recipient == null) {
            if (other.recipient != null)
                return false;
        } else if (!recipient.equals(other.recipient))
            return false;

        return true;
    }

    public DBFile getFile() {
        return file;
    }

    public void setFile(DBFile file) {
        this.file = file;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public LocalDateTime getDownloadDate() {
        return downloadDate;
    }

    public void setDownloadDate(LocalDateTime downloadDate) {
        this.downloadDate = downloadDate;
    }

    @Override
    public String toString() {
        return "DBFileLogs [downloadDate=" + downloadDate + ", downloadLink=" + downloadLink + ", file=" + file
                + ", recipient=" + recipient + "]";
    }

}
