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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import eu.europa.circabc.eushare.model.LastUpload;

public class LastUploadDTO {
    private String uploader_email;
    private String share_email;
    private String filename;
    private Long file_size;
    private String path;
    private String status;
    private LocalDateTime created;
    private String shorturl;
    private Boolean download_notification;

    public LastUploadDTO(String uploader_email, String share_email, String filename, Long file_size, String path,
            String status, LocalDateTime created, String shorturl, Boolean download_notification) {
        this.uploader_email = uploader_email;
        this.share_email = share_email;
        this.filename = filename;
        this.file_size = file_size;
        this.path = path;
        this.status = status;
        this.created = created;
        this.shorturl = shorturl;
        this.download_notification = download_notification;
    }

    public LastUpload toLastUpload() {
        LastUpload lastUpload = new LastUpload();
        lastUpload.setUploaderEmail(this.uploader_email);
        lastUpload.setShareEmail(this.share_email);
        lastUpload.setFilename(this.filename);
        lastUpload.setFileSize(this.file_size);
        lastUpload.setPath(this.path);
        lastUpload.setStatus(this.status);

        if (this.created != null) {
            lastUpload.setCreated(OffsetDateTime.of(this.created, ZoneOffset.UTC));
        }

        lastUpload.setShorturl(this.shorturl);
        lastUpload.setDownloadNotification(this.download_notification);

        return lastUpload;
    }

    public String getUploader_email() {
        return uploader_email;
    }

    public void setUploader_email(String uploader_email) {
        this.uploader_email = uploader_email;
    }

    public String getShare_email() {
        return share_email;
    }

    public void setShare_email(String share_email) {
        this.share_email = share_email;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getFile_size() {
        return file_size;
    }

    public void setFile_size(Long file_size) {
        this.file_size = file_size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
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

}
