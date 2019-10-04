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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.circabc.easyshare.model.FileBasics;
import com.circabc.easyshare.model.FileInfoRecipient;
import com.circabc.easyshare.model.FileInfoUploader;
import com.circabc.easyshare.model.RecipientWithLink;

import org.springframework.security.crypto.bcrypt.BCrypt;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Files")
@Getter
@Setter
public class DBFile {
    @Column(nullable = false)
    private LocalDate expirationDate;

    @Column(nullable = false)
    private String filename;

    @Id
    @Column(name = "FILE_ID", nullable = false, unique = true)
    private String id;

    @Column(nullable = false)
    private LocalDateTime lastModified = LocalDateTime.now();

    private String password;

    @Column(nullable = false)
    private String path;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "file") //, cascade = CascadeType.ALL, orphanRemoval=true
    private Set<DBUserFile> sharedWith;


    public void addUsersSharedWith(DBUser dbUser, String downloadId, String message) {
        DBUserFile dbUserFile = new DBUserFile(downloadId, dbUser, this, message);
        sharedWith.add(dbUserFile);
        dbUser.getFilesReceived().add(dbUserFile);
    }
    
    public void addUsersSharedWith(DBUser dbUser, String downloadId) {
        DBUserFile dbUserFile = new DBUserFile(downloadId, dbUser, this);
        sharedWith.add(dbUserFile);
        dbUser.getFilesReceived().add(dbUserFile);
	}

	public void removeUsersSharedWith(DBUser dbUser, String downloadId) {
        DBUserFile dbUserFile = new DBUserFile(downloadId, dbUser, this);
        dbUser.getFilesReceived().remove(dbUserFile);
        sharedWith.remove(dbUserFile);
        dbUserFile.setFile(null);
        dbUserFile.setReceiver(null);
	}

    @Column(name = "fileSize", nullable = false)
    private long size;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ALLOCATED;

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "Fk_to_uploader"))
    private DBUser uploader;

    public DBFile(String id, DBUser uploader, Collection<DBUserFile> sharedWith, String filename, long size,
            LocalDate expirationDate, String path) {
        this(id, uploader, sharedWith, filename, size, expirationDate, path, null);
    }

    public DBFile(String id, DBUser uploader, Collection<DBUserFile> sharedWith, String filename, long size,
            LocalDate expirationDate, String path, String password) { // NOSONAR
        this.id = id;
        this.uploader = uploader;
        this.sharedWith = new HashSet<>(sharedWith);
        this.filename = filename;
        this.size = size;
        this.expirationDate = expirationDate;
        this.path = path;

        if (password != null) {
            this.password = BCrypt.hashpw(password, BCrypt.gensalt());
        }
    }

    private DBFile() {
    }

    public FileInfoRecipient toFileInfoRecipient(String recipientId) {
        FileInfoRecipient fileInfoRecipient = new FileInfoRecipient();
        fileInfoRecipient.setExpirationDate(this.expirationDate);
        fileInfoRecipient.setHasPassword(this.password != null);
        fileInfoRecipient.setName(this.filename);
        fileInfoRecipient.setSize(new BigDecimal(this.size));
        fileInfoRecipient.setUploaderName(this.uploader.getName());
        for (DBUserFile dbUserFile : this.getSharedWith()) {
            if(dbUserFile.getReceiver().getId().equals(recipientId)) {
                fileInfoRecipient.setFileId(dbUserFile.getDownloadId());
            }
        }
        return fileInfoRecipient;
    }

    public FileInfoUploader toFileInfoUploader() {
        FileInfoUploader fileInfoUploader = new FileInfoUploader();
        List<RecipientWithLink> sharedWithRecipients = this.getSharedWith().stream()
                .map(dbUserFile -> dbUserFile.toRecipientWithLink()).collect(Collectors.toList());
        fileInfoUploader.setExpirationDate(this.expirationDate);
        fileInfoUploader.setHasPassword(this.password != null);
        fileInfoUploader.setName(this.filename);
        fileInfoUploader.setSize(new BigDecimal(this.size));
        fileInfoUploader.setFileId(this.getId());
        fileInfoUploader.setSharedWith(sharedWithRecipients);
        return fileInfoUploader;
    }

    public FileBasics toFileBasics() {
        FileBasics fileBasics = new FileBasics();
        fileBasics.setExpirationDate(this.expirationDate);
        fileBasics.setHasPassword(this.password != null);
        fileBasics.setName(this.filename);
        fileBasics.setSize(new BigDecimal(this.size));
        return fileBasics;
    }

    public void setStatus(Status status) {
        this.status = status;
        this.lastModified = LocalDateTime.now();
    }

    public enum Status {
        ALLOCATED, UPLOADING, AVAILABLE, DELETED
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DBFile)) {
            return false;
        }
        DBFile other = (DBFile) o;
        return (id != null && id.equals(other.getId()));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
