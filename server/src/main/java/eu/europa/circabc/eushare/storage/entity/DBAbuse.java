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

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eu.europa.circabc.eushare.model.AbuseReport;
import eu.europa.circabc.eushare.model.EnumConverter;
import eu.europa.circabc.eushare.storage.dto.AbuseReportDetailsDTO;

@Entity
@NamedNativeQuery(name = "AbuseReportDetailsDTO.findAllDetails", query = "SELECT " +
        "a.abuse_id AS id, " +
        "a.date AS date, " +
        "a.description AS description, " +
        "f.file_id AS file_id," +
        "s.shorturl AS shorturl, " +
        "a.reason AS reason, " +
        "a.reporter AS reporter, " +
        "a.status AS status, " +
        "f.filename AS filename, " +
        "f.file_size AS filesize, " +
        "u.email AS uploader_email, " +
        "u.name AS uploader_name, " +
        "u.status AS uploader_status " +
        "FROM " +
        "abuse a " +
        "JOIN " +
        "shares s ON a.short_url = s.shorturl " +
        "JOIN " +
        "files f ON s.file_file_id = f.file_id " +
        "JOIN " +
        "users u ON f.uploader_id = u.id", resultSetMapping = "AbuseReportDetailsDTOMapping")

@SqlResultSetMapping(name = "AbuseReportDetailsDTOMapping", classes = @ConstructorResult(targetClass = AbuseReportDetailsDTO.class, columns = {
        @ColumnResult(name = "id"),
        @ColumnResult(name = "reporter"),
        @ColumnResult(name = "file_id"),
        @ColumnResult(name = "shorturl"),
        @ColumnResult(name = "reason"),
        @ColumnResult(name = "description"),
        @ColumnResult(name = "date", type = LocalDate.class),
        @ColumnResult(name = "status", type = String.class),
        @ColumnResult(name = "filename"),
        @ColumnResult(name = "filesize", type = BigDecimal.class),
        @ColumnResult(name = "uploader_email"),
        @ColumnResult(name = "uploader_name"),
        @ColumnResult(name = "uploader_status")
}))

@Table(name = "Abuse")
public class DBAbuse {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ABUSE_ID", nullable = false, unique = true)
    private String id;

    @Column(nullable = false)
    private String reporter;

    @Column(nullable = false)
    private String fileId;

    @Column(nullable = false)
    private String shortUrl;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public enum Status {
        WAITING,
        APPROVED,
        DENIED
    }

    public DBAbuse() {
        this.date = LocalDate.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public static DBAbuse toDBAbuse(AbuseReport abuseReport) {
        DBAbuse dbAbuse = new DBAbuse();
        dbAbuse.setReporter(abuseReport.getReporter());
        dbAbuse.setFileId(abuseReport.getFileId());
        dbAbuse.setReason(abuseReport.getReason());
        dbAbuse.setDescription(abuseReport.getDescription());
        dbAbuse.setDate(abuseReport.getDate());

        DBAbuse.Status dbAbuseStatus = EnumConverter.convert(abuseReport.getStatus(), DBAbuse.Status.class);
        dbAbuse.setStatus(dbAbuseStatus);

        return dbAbuse;
    }

    public AbuseReport toAbuseReport() {
        AbuseReport abuseReport = new AbuseReport();
        abuseReport.setReporter(this.getReporter());
        abuseReport.setFileId(this.getFileId());
        abuseReport.setReason(this.getReason());
        abuseReport.setDescription(this.getDescription());
        abuseReport.setDate(this.getDate());

        AbuseReport.StatusEnum abuseReportStatus = EnumConverter.convert(this.getStatus(),
                AbuseReport.StatusEnum.class);
        abuseReport.setStatus(abuseReportStatus);

        return abuseReport;
    }

    

}
