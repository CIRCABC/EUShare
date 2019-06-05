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
}