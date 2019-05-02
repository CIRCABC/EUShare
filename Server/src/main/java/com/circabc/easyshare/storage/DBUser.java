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

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import com.circabc.easyshare.model.UserInfo;
import com.circabc.easyshare.model.UserSpace;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "Users")
public class DBUser {
    @Getter
    @Setter
    @OneToMany(fetch = FetchType.EAGER)
    private Set<DBFile> sharedFiles = new HashSet<>();

    @Id
    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.INTERNAL;

    @Getter
    @Setter
    private long totalSpace;

    @Getter
    @Setter
    @OneToMany(mappedBy = "uploader", fetch = FetchType.EAGER)
    private Set<DBFile> uploads = new HashSet<>();

    @Getter
    @Setter
    @Column(unique=true)
    private String username;

    @Getter
    @Setter
    private String password;

    private DBUser() {
    }

    private DBUser(String id, long totalSpace, Role role) {
        this.id = id;
        this.totalSpace = totalSpace;
        this.role = role;
    }

    /**
     * Create a new user with specified role {@code EXTERNAL}
     */
    public static DBUser createExternalUser(String mail) {
        return new DBUser(mail, 0, Role.EXTERNAL);
    }

    /**
     * Create a new user with specified role {@code INTERNAL}
     *
     * @throws IllegalArgumentException If {@code totalSpace < 0}
     */
    public static DBUser createInternalUser(String id, String username, String password, long totalSpace) {
        if (totalSpace < 0) {
            throw new IllegalArgumentException();
        }

        DBUser dbUser = new DBUser(id, totalSpace, Role.INTERNAL);
        dbUser.setUsername(username);
        dbUser.setPassword(password);
        return dbUser;
    }

    public long getFreeSpace() {
        return this.totalSpace - this.getUsedSpace();
    }

    @Transient
    private long getUsedSpace() {
        return this.uploads.stream().mapToLong(DBFile::getSize).sum();
    }

    /**
     * Convert to {@link UserInfo} object
     */
    public UserInfo toUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setTotalSpace(new BigDecimal(this.totalSpace));
        List<String> uploadedFiles = new ArrayList<>();
        long totalSize = 0;
        for (DBFile upload : this.uploads) {
            if (upload.getStatus() == DBFile.Status.AVAILABLE) {
                uploadedFiles.add(upload.getId());
                totalSize = totalSize + upload.getSize();
            }
        }
        userInfo.setUsedSpace(new BigDecimal(totalSize));
        userInfo.setId(this.id);
        userInfo.setUploadedFiles(uploadedFiles);
        userInfo.setSharedFiles(this.sharedFiles.stream().filter(dbFile -> dbFile.getStatus().equals(DBFile.Status.AVAILABLE)).map(dbFile -> dbFile.getId()).collect(Collectors.toList()));
        userInfo.isAdmin(this.role.equals(Role.ADMIN));
        return userInfo;
    }

    public UserSpace toUserSpace() {
        UserSpace userSpace = new UserSpace();
        userSpace.setTotalSpace(new BigDecimal(this.totalSpace));
        long totalSize = 0;
        for (DBFile upload : this.uploads) {
            if (upload.getStatus() == DBFile.Status.AVAILABLE) {
                totalSize = totalSize + upload.getSize();
            }
        }
        userSpace.setUsedSpace(new BigDecimal(totalSize));
        return userSpace;
    }

    public enum Role {
        EXTERNAL,
        INTERNAL,
        ADMIN
    }
}
