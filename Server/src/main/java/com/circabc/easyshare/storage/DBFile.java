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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.circabc.easyshare.model.FileBasics;
import com.circabc.easyshare.model.FileInfoRecipient;
import com.circabc.easyshare.model.FileInfoUploader;
import com.circabc.easyshare.model.Recipient;

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
    private String id;

    @Column(nullable = false)
    private LocalDateTime lastModified = LocalDateTime.now();

    private String password;

    @Column(nullable = false)
    private String path;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<DBUser> sharedWith;

    @Column(name = "fileSize", nullable = false)
    private long size;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ALLOCATED;

    @ManyToOne(optional = false)
    private DBUser uploader;

    DBFile(String id, DBUser uploader, Collection<DBUser> sharedWith, String filename, long size, LocalDate expirationDate, String path) {
        this(id, uploader, sharedWith, filename, size, expirationDate, path, null);
    }

    public DBFile(String id, DBUser uploader, Collection<DBUser> sharedWith, String filename, long size, LocalDate expirationDate, String path, String password) {
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

    public FileInfoRecipient toFileInfoRecipient() {
        FileInfoRecipient fileInfoRecipient = new FileInfoRecipient();
        fileInfoRecipient.setExpirationDate(this.expirationDate);
        fileInfoRecipient.setHasPassword(this.password != null);
        fileInfoRecipient.setName(this.filename);
        fileInfoRecipient.setSize(new BigDecimal(this.size));
        fileInfoRecipient.uploader(this.uploader.getId());
        return fileInfoRecipient;
    }

    public FileInfoUploader toFileInfoUploader() {
        FileInfoUploader fileInfoUploader = new FileInfoUploader();
        List<Recipient> sharedWithRecipients = new ArrayList<>();
        for (DBUser dbUser : this.sharedWith) {
            Recipient recipient = new Recipient();
            recipient.setEmailOrID(dbUser.getId());
            sharedWithRecipients.add(recipient);
        }
        fileInfoUploader.setExpirationDate(this.expirationDate);
        fileInfoUploader.setHasPassword(this.password!=null);
        fileInfoUploader.setName(this.filename);
        fileInfoUploader.setSize(new BigDecimal(this.size));
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
        ALLOCATED,
        UPLOADING,
        AVAILABLE,
        DELETED
    }
}
