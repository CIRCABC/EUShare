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
import java.time.LocalDate;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import eu.europa.circabc.eushare.model.AbuseReportDetails;
import eu.europa.circabc.eushare.storage.entity.DBAbuse.Status;
import eu.europa.circabc.eushare.model.EnumConverter;

public class AbuseReportDetailsDTO {
    private static final long serialVersionUID = 1L;

    private String id;

    private String reporter;
    private String fileId;
    private String shortUrl;
    private String reason;
    private String description;
    private LocalDate date;
    @Enumerated(value = EnumType.STRING)
    private Status status;
    private String filename;
    private BigDecimal filesize;
    private String uploaderEmail;
    private String uploaderName;
    private String uploaderStatus;

    private AbuseReportDetailsDTO() {
    }

    public AbuseReportDetailsDTO(String id, String reporter, String fileId, String shortUrl, String reason,
            String description,
            LocalDate date, String statusStr, String filename, BigDecimal filesize, String uploaderEmail,
            String uploaderName, String uploaderStatus) {
        this.id = id;
        this.reporter = reporter;
        this.fileId = fileId;
        this.shortUrl = shortUrl;
        this.reason = reason;
        this.description = description;
        this.date = date;
        this.status = Status.valueOf(statusStr);
        this.filename = filename;
        this.filesize = filesize;
        this.uploaderEmail = uploaderEmail;
        this.uploaderName = uploaderName;
        this.uploaderStatus = uploaderStatus;
    }

    public AbuseReportDetails toAbuseReportDetails() {
        AbuseReportDetails abuseReportDetails = new AbuseReportDetails();
        abuseReportDetails.setID(this.id);
        abuseReportDetails.setReporter(this.reporter);
        abuseReportDetails.setFileId(this.fileId);
        abuseReportDetails.setShortUrl(this.shortUrl);
        abuseReportDetails.setReason(this.reason);
        abuseReportDetails.setDescription(this.description);
        abuseReportDetails.setDate(this.date);

        AbuseReportDetails.StatusEnum abuseReportDetailsStatus = EnumConverter.convert(this.status,
                AbuseReportDetails.StatusEnum.class);
        abuseReportDetails.setStatus(abuseReportDetailsStatus);

        abuseReportDetails.setFilename(this.filename);
        abuseReportDetails.setFilesize(this.filesize);
        abuseReportDetails.setUploaderEmail(this.uploaderEmail);
        abuseReportDetails.setUploaderName(this.uploaderName);
        abuseReportDetails.setUploaderStatus(this.uploaderStatus);
        return abuseReportDetails;
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

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getfileId() {
        return fileId;
    }

    public void setfileId(String fileId) {
        this.fileId = fileId;
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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public BigDecimal getFilesize() {
        return filesize;
    }

    public void setFilesize(BigDecimal filesize) {
        this.filesize = filesize;
    }

    public String getUploaderEmail() {
        return uploaderEmail;
    }

    public void setUploaderEmail(String uploaderEmail) {
        this.uploaderEmail = uploaderEmail;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
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

    public String getUploaderStatus() {
        return uploaderStatus;
    }

    public void setUploaderStatus(String uploaderStatus) {
        this.uploaderStatus = uploaderStatus;
    }

}
