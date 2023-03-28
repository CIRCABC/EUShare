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

import eu.europa.circabc.eushare.model.UserInfo;
import eu.europa.circabc.eushare.model.UserSpace;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
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
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "Users")
public class DBUser {

  @Id
  @Column(nullable = false)
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  private String id;

  @Enumerated(value = EnumType.STRING)
  @Column(nullable = false)
  private Role role = eu.europa.circabc.eushare.storage.DBUser.Role.INTERNAL;

  @Column(nullable = false)
  private long totalSpace;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "uploader")
  private Set<DBFile> filesUploaded = new HashSet<>();

  @Column(nullable = true, unique = true)
  private String username;


  @Column(nullable = true, columnDefinition = "DATETIME DEFAULT NULL")
  private LocalDateTime lastLogged = LocalDateTime.now();


  // Is the username used for login

  @Column(nullable = true, unique = true)
  private String email;

  // Is the email used for login is nullable because a user can represent a link recipient

  @Column(nullable = true)
  private String name;

  // Is the GivenName of the INTERNAL user !!OR!! the link name of the EXTERNAL user

  @Column(nullable = true, unique = true)
  private String apiKey;

  private DBUser() {}

  private DBUser(long totalSpace, Role role) {
    this.totalSpace = totalSpace;
    this.role = role;
  }

  /**
   * Create a new user with specified role {@code INTERNAL}
   *
   * @throws IllegalArgumentException If {@code totalSpace < 0}
   */
  public static DBUser createInternalUser(
    String email,
    String name,
    long totalSpace,
    String username
  ) {
    if (totalSpace < 0) {
      throw new IllegalArgumentException();
    }
    DBUser dbUser = new DBUser(totalSpace, Role.INTERNAL);
    String lowerCaseEmail = email.toLowerCase();
    dbUser.setEmail(lowerCaseEmail);

    if (name == null || name.isEmpty()) {
      name =
        eu.europa.circabc.eushare.utils.StringUtils.emailToGivenName(email);
    }
    dbUser.setName(name);
    dbUser.setTotalSpace(totalSpace);
    dbUser.setUsername(username);
    dbUser.setLastLogged(LocalDateTime.now());
    return dbUser;
  }

  public long getFreeSpace() {
    return this.totalSpace - this.getUsedSpace();
  }

  @Transient
  private long getUsedSpace() {
    return this.filesUploaded.stream()
      .filter(dbFile ->
        dbFile.getStatus().equals(DBFile.Status.AVAILABLE) ||
        dbFile.getStatus().equals(DBFile.Status.UPLOADING)
      )
      .mapToLong(DBFile::getSize)
      .sum();
  }

  /**
   * Convert to {@link UserInfo} object
   */
  public UserInfo toUserInfo() {
    UserInfo userInfo = new UserInfo();
    userInfo.setTotalSpace(new BigDecimal(this.totalSpace));
    long totalSize = 0;
    for (DBFile upload : this.filesUploaded) {
      if (upload.getStatus() == DBFile.Status.AVAILABLE) {
        totalSize = totalSize + upload.getSize();
      }
    }
    userInfo.setUsedSpace(new BigDecimal(totalSize));
    userInfo.setId(this.getId());
    userInfo.setGivenName(this.getName());
    userInfo.setLoginUsername(this.getUsername());
    if(this.role.equals(Role.ADMIN))
      userInfo.setRole(UserInfo.RoleEnum.ADMIN);
    if(this.role.equals(Role.INTERNAL))
      userInfo.setRole(UserInfo.RoleEnum.INTERNAL);
    if(this.role.equals(Role.API_KEY))
      userInfo.setRole(UserInfo.RoleEnum.API_KEY);
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
    INTERNAL, // An active user. Has an email, a name and a username.
    ADMIN, // An internal user with extra advantage
    API_KEY
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

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public long getTotalSpace() {
    return totalSpace;
  }

  public void setTotalSpace(long totalSpace) {
    this.totalSpace = totalSpace;
  }

  public Set<DBFile> getFilesUploaded() {
    return filesUploaded;
  }

  public void setFilesUploaded(Set<DBFile> filesUploaded) {
    this.filesUploaded = filesUploaded;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public LocalDateTime getLastLogged() {
    return lastLogged;
  }

  public void setLastLogged(LocalDateTime lastLogged) {
    this.lastLogged = lastLogged;
  }

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  
}
