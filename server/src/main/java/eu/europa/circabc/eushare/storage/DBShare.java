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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import eu.europa.circabc.eushare.model.Recipient;

@Entity
@Table(name = "shares")
public class DBShare {

    @Id
    private String downloadId;

    @Column(nullable = false)
    private String email;

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "Fk_to_file"))
    private DBFile file;

    @Column(nullable = false,length = 50)
    private String shorturl;

    private String message;

    private DBShare() {

    }

    public DBShare(String downloadId, String email, DBFile file) {
        this.downloadId = downloadId;
        this.email = email;
        this.file = file;
    }

    public DBShare(String downloadId, String email, DBFile file, String message) {
        this.downloadId = downloadId;
        this.email = email;
        this.file = file;
        if (message != null) {
            this.message = message;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DBShare)) {
            return false;
        }
        DBShare other = (DBShare) o;
        return (downloadId != null && downloadId.equals(other.getDownloadId()));
    }

    @Override
    public int hashCode() {
        return downloadId.hashCode();
    }

    public Recipient toRecipient() {
        Recipient recipient = new Recipient();
        recipient.setEmail(this.getEmail());
        recipient.setShortUrl(this.getShorturl());
        recipient.setMessage(this.getMessage());
        return recipient;
    }

    public String getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String receiver) {
        this.email = receiver;
    }

    public DBFile getFile() {
        return file;
    }

    public void setFile(DBFile file) {
        this.file = file;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getShorturl() {
        return shorturl;
    }

    public void setShorturl(String shorturl) {
        this.shorturl = shorturl;
    }

}