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

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import eu.europa.circabc.eushare.model.Recipient;
import eu.europa.circabc.eushare.model.RecipientWithLink;


@Entity
@Table(name = "users_to_files")
public class DBUserFile {

    @Id
    private String downloadId;

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey= @ForeignKey(name = "Fk_to_user"))
    private DBUser receiver;

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey= @ForeignKey(name = "Fk_to_file"))
    private DBFile file;

    private String message;

    private DBUserFile() {

    }

    public DBUserFile(String downloadId, DBUser user, DBFile file) {
        this.downloadId =  downloadId;
        this.receiver = user;
        this.file = file;
    }

    public DBUserFile(String downloadId, DBUser user, DBFile file, String message) {
        this.downloadId =  downloadId;
        this.receiver = user;
        this.file = file;
        if(message != null) {
            this.message = message;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DBUserFile)){
            return false;
        }
        DBUserFile other = (DBUserFile) o;
        return (downloadId != null && downloadId.equals(other.getDownloadId()));
    }
 
    @Override
    public int hashCode() {
        return downloadId.hashCode();
    }

    public Recipient toRecipient() {
        Recipient recipient = new Recipient();
        if (this.getReceiver().getEmail() != null){
            recipient.setEmailOrName(this.getReceiver().getEmail());
            recipient.setSendEmail(true);
        } else {
            recipient.setEmailOrName(this.getReceiver().getName());
            recipient.setSendEmail(false);
        }
        recipient.setMessage(this.getMessage());
        return recipient;
    }

    public RecipientWithLink toRecipientWithLink(){
        RecipientWithLink recipientWithLink = new RecipientWithLink();
        if (this.getReceiver().getEmail() != null){
            recipientWithLink.setEmailOrName(this.getReceiver().getEmail());
            recipientWithLink.setSendEmail(true);
        } else {
            recipientWithLink.setEmailOrName(this.getReceiver().getName());
            recipientWithLink.setSendEmail(false);
        }
        recipientWithLink.setRecipientId(this.getReceiver().getId());
        recipientWithLink.setMessage(this.getMessage());
        recipientWithLink.setDownloadLink(this.getDownloadId());
        return recipientWithLink;
    }

    public String getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
    }

    public DBUser getReceiver() {
        return receiver;
    }

    public void setReceiver(DBUser receiver) {
        this.receiver = receiver;
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

    
}