/**
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */

package com.circabc.easyshare.storage;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.circabc.easyshare.model.Recipient;
import com.circabc.easyshare.model.RecipientWithLink;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users_to_files")
public class DBUserFile {

    @Id
    @Getter
    @Setter
    private String downloadId;

    @Getter
    @Setter
    @ManyToOne(optional = false)
    @JoinColumn(foreignKey= @ForeignKey(name = "Fk_to_user"))
    private DBUser receiver;

    @Getter
    @Setter
    @ManyToOne(optional = false)
    @JoinColumn(foreignKey= @ForeignKey(name = "Fk_to_file"))
    private DBFile file;

    @Getter
    @Setter
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
        return 31;
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
}