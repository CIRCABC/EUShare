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

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import eu.europa.circabc.eushare.model.LastDownload;

public class LastDownloadDTO {
    private String uploader_email;
    private String recipient;
    private String filename;
    private String path;
    private String password;
    private String shorturl;
    private Boolean download_notification;
    private LocalDateTime download_date;



    
    public LastDownloadDTO(String uploader_email, String recipient, String filename, String path, String password,
            String shorturl, Boolean download_notification, LocalDateTime download_date) {
        this.uploader_email = uploader_email;
        this.recipient = recipient;
        this.filename = filename;
        this.path = path;
        this.password = password;
        this.shorturl = shorturl;
        this.download_notification = download_notification;
        this.download_date = download_date;
    }
    
    public LastDownload toLastDownload() {
    LastDownload lastDownload = new LastDownload();

    lastDownload.setUploaderEmail(this.getUploader_email());
    lastDownload.setRecipient(this.getRecipient());
    lastDownload.setFilename(this.getFilename());
    lastDownload.setPath(this.getPath());
    lastDownload.setPassword(this.getPassword());
    lastDownload.setShorturl(this.getShorturl());
    lastDownload.setDownloadNotification(this.getDownload_notification());

    // Convert LocalDateTime to OffsetDateTime
    if(this.getDownload_date() != null) {
        lastDownload.setDownloadDate(this.getDownload_date().atOffset(ZoneOffset.UTC));
    }

    return lastDownload;
}

    public String getUploader_email() {
        return uploader_email;
    }
    public void setUploader_email(String uploader_email) {
        this.uploader_email = uploader_email;
    }
    public String getRecipient() {
        return recipient;
    }
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getShorturl() {
        return shorturl;
    }
    public void setShorturl(String shorturl) {
        this.shorturl = shorturl;
    }
    public Boolean getDownload_notification() {
        return download_notification;
    }
    public void setDownload_notification(Boolean download_notification) {
        this.download_notification = download_notification;
    }
    public LocalDateTime getDownload_date() {
        return download_date;
    }
    public void setDownload_date(LocalDateTime download_date) {
        this.download_date = download_date;
    }
    
}
