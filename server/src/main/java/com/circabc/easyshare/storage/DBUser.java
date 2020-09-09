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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.circabc.easyshare.model.UserInfo;
import com.circabc.easyshare.model.UserSpace;

import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Users")
public class DBUser {
    @Getter
    @Setter
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "receiver")
    private Set<DBUserFile> filesReceived = new HashSet<>();

    @Id
    @Getter
    @Setter
    @Column(nullable = false)
    @GeneratedValue(generator = "prod-generator")
    @GenericGenerator(name = "prod-generator", strategy = "com.circabc.easyshare.storage.SecureRandomIdentifierGenerator")
    private String id;

    @Getter
    @Setter
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Role role = com.circabc.easyshare.storage.DBUser.Role.INTERNAL;

    @Getter
    @Setter
    @Column(nullable = false)
    private long totalSpace;

    @Getter
    @Setter
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "uploader")
    private Set<DBFile> filesUploaded = new HashSet<>();

    @Getter
    @Setter
    @Column(nullable = true, unique = true)
    private String username;
    // Is the username used for login

    @Getter
    @Setter
    @Column(nullable = true, unique = true)
    private String email;
    // Is the email used for login is nullable because a user can represent a link recipient

    @Getter
    @Setter
    @Column(nullable = true)
    private String name;
    // Is the GivenName of the INTERNAL user !!OR!! the link name of the EXTERNAL user

    private DBUser() {

    }

    private DBUser(long totalSpace, Role role) {
        this.totalSpace = totalSpace;
        this.role = role;
    }

     /**
     * Create a new user with specified role {@code LINK}
     */
    public static DBUser createLinkUser(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        DBUser dbUser = new DBUser(0, Role.LINK);
        dbUser.setName(name);
        return dbUser;
    }

    /**
     * Create a new user with specified role {@code EXTERNAL}
     */
    public static DBUser createExternalUser(String mail, String name) throws IllegalArgumentException {
        if (mail == null && name == null) {
            throw new IllegalArgumentException();
        }
        DBUser dbUser = new DBUser(0, Role.EXTERNAL);
        if (mail != null) {
            String upperCaseEmail = mail.toUpperCase();
            dbUser.setEmail(upperCaseEmail);
        }
        dbUser.setName(name);
        return dbUser;
    }

    /**
     * Create a new user with specified role {@code INTERNAL}
     *
     * @throws IllegalArgumentException If {@code totalSpace < 0}
     */
    public static DBUser createInternalUser(String email, String name, long totalSpace,
            String username) {
        if (totalSpace < 0) {
            throw new IllegalArgumentException();
        }
        DBUser dbUser = new DBUser(totalSpace, Role.INTERNAL);
        String upperCaseEmail = email.toUpperCase();
        dbUser.setEmail(upperCaseEmail);

        if (name == null || name.isEmpty()) {
            name = com.circabc.easyshare.utils.StringUtils.emailToGivenName(email);
        }
        dbUser.setName(name);
        dbUser.setTotalSpace(totalSpace);
        dbUser.setUsername(username);
        return dbUser;
    }

    public long getFreeSpace() {
        return this.totalSpace - this.getUsedSpace();
    }

    @Transient
    private long getUsedSpace() {
        return this.filesUploaded.stream()
                .filter(dbFile -> dbFile.getStatus().equals(DBFile.Status.AVAILABLE)
                        || dbFile.getStatus().equals(DBFile.Status.ALLOCATED)
                        || dbFile.getStatus().equals(DBFile.Status.UPLOADING))
                .mapToLong(DBFile::getSize).sum();
    }

    /**
     * Convert to {@link UserInfo} object
     */
    public UserInfo toUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setTotalSpace(new BigDecimal(this.totalSpace));
        List<String> uploadedFiles = new ArrayList<>();
        long totalSize = 0;
        for (DBFile upload : this.filesUploaded) {
            if (upload.getStatus() == DBFile.Status.AVAILABLE) {
                uploadedFiles.add(upload.getId());
                totalSize = totalSize + upload.getSize();
            }
        }
        userInfo.setUsedSpace(new BigDecimal(totalSize));
        userInfo.setId(this.getId());
        userInfo.setGivenName(this.getName());
        userInfo.setLoginUsername(this.getUsername());
        userInfo.isAdmin(this.role.equals(Role.ADMIN));
        userInfo.setEmail(this.email);
        return userInfo;
    }

    public UserSpace toUserSpace() {
        UserSpace userSpace = new UserSpace();
        userSpace.setTotalSpace(new BigDecimal(this.totalSpace));
        long totalSize = 0;
        for (DBFile upload : this.filesUploaded) {
            if (upload.getStatus() == DBFile.Status.AVAILABLE) {
                totalSize = totalSize + upload.getSize();
            }
        }
        userSpace.setUsedSpace(new BigDecimal(totalSize));
        return userSpace;
    }

    public enum Role {
        LINK, // A user representing a share by link
        EXTERNAL, // A user added by another, non active yet. Has an email address and nothing else.
        INTERNAL, // An active user. Has an email, a name and a username.
        ADMIN, // An internal user with extra advantage
    }

    public String toSpringSecurityRole(Role role) {
        return role.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DBUser)) {
            return false;
        }
        DBUser other = (DBUser) o;
        return (id != null && id.equals(other.getId()));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
