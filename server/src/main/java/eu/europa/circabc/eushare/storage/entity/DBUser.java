/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.storage.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToMany;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import eu.europa.circabc.eushare.model.EnumConverter;
import eu.europa.circabc.eushare.model.UserInfo;
import eu.europa.circabc.eushare.model.UserSpace;
import eu.europa.circabc.eushare.storage.dto.UserInfoDTO;

@Entity
@SqlResultSetMapping(name = "UserInfoDTOMapping", classes = @ConstructorResult(targetClass = UserInfoDTO.class, columns = {
    @ColumnResult(name = "id", type = String.class),
    @ColumnResult(name = "role", type = String.class),
    @ColumnResult(name = "status", type = String.class),
    @ColumnResult(name = "username", type = String.class),
    @ColumnResult(name = "name", type = String.class),
    @ColumnResult(name = "email", type = String.class),
    @ColumnResult(name = "total_space", type = long.class),
    @ColumnResult(name = "used_space", type = long.class),
    @ColumnResult(name = "files_count", type = long.class),

}))

@NamedNativeQuery(
    name = "UserInfoDTO.findAllByEmailOrName",
    query = "SELECT id, role, users.status, username, name, email, total_space, COALESCE(SUM(files.file_size), 0) AS used_space, COALESCE(COUNT(files.file_id), 0) AS files_count "
    + "FROM users LEFT JOIN files ON users.id = files.uploader_id "
    + "WHERE (email LIKE LOWER(CONCAT(:start,'%')) OR LOWER(name) LIKE LOWER(CONCAT(:start,'%')) OR LOWER(name) LIKE LOWER(CONCAT('% ',:start,'%'))) "
    + "GROUP BY id, role, username, name, email, total_space "
    + "ORDER BY "
    + "CASE WHEN :sortField = 'id' AND :sortOrder = 'ASC' THEN id END ASC, "
    + "CASE WHEN :sortField = 'role' AND :sortOrder = 'ASC' THEN role END ASC, "
    + "CASE WHEN :sortField = 'status' AND :sortOrder = 'ASC' THEN users.status END ASC, "
    + "CASE WHEN :sortField = 'username' AND :sortOrder = 'ASC' THEN username END ASC, "
    + "CASE WHEN :sortField = 'name' AND :sortOrder = 'ASC' THEN name END ASC, "
    + "CASE WHEN :sortField = 'email' AND :sortOrder = 'ASC' THEN email END ASC, "
    + "CASE WHEN :sortField = 'total_space' AND :sortOrder = 'ASC' THEN total_space END ASC, "
    + "CASE WHEN :sortField = 'id' AND :sortOrder = 'DESC' THEN id END DESC, "
    + "CASE WHEN :sortField = 'role' AND :sortOrder = 'DESC' THEN role END DESC, "
    + "CASE WHEN :sortField = 'status' AND :sortOrder = 'DESC' THEN users.status END DESC, "
    + "CASE WHEN :sortField = 'username' AND :sortOrder = 'DESC' THEN username END DESC, "
    + "CASE WHEN :sortField = 'name' AND :sortOrder = 'DESC' THEN name END DESC, "
    + "CASE WHEN :sortField = 'email' AND :sortOrder = 'DESC' THEN email END DESC, "
    + "CASE WHEN :sortField = 'total_space' AND :sortOrder = 'DESC' THEN total_space END DESC, "
    + "CASE WHEN :sortField = 'usedSpace' AND :sortOrder = 'ASC' THEN used_space END ASC, "
    + "CASE WHEN :sortField = 'filesCount' AND :sortOrder = 'ASC' THEN files_count END ASC, "
    + "CASE WHEN :sortField = 'usedSpace' AND :sortOrder = 'DESC' THEN used_space END DESC, "
    + "CASE WHEN :sortField = 'filesCount' AND :sortOrder = 'DESC' THEN files_count END DESC",
    resultSetMapping = "UserInfoDTOMapping"
)

@NamedNativeQuery(name = "DBUser.countUsers", query = "SELECT COUNT(*) as count FROM users", resultSetMapping = "countUsersMapping")
@SqlResultSetMapping(name = "countUsersMapping", columns = @ColumnResult(name = "count", type = Long.class))


@Table(name = "Users")
public class DBUser {

  @Id
  @Column(nullable = false)
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  private String id;

  @Enumerated(value = EnumType.STRING)
  @Column(nullable = false)
  private Role role = eu.europa.circabc.eushare.storage.entity.DBUser.Role.INTERNAL;

  @Enumerated(value = EnumType.STRING)
  @Column(nullable = false)
  private Status status = eu.europa.circabc.eushare.storage.entity.DBUser.Status.REGULAR;

  @Column(nullable = false)
  private long totalSpace;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "uploader")
  private Set<DBFile> filesUploaded = new HashSet<>();

  @Column(nullable = true, unique = true)
  private String username;

  @Column(nullable = true)
  private LocalDateTime lastLogged = LocalDateTime.now();

  @Column(nullable = true)
  private LocalDateTime creationDate = LocalDateTime.now();

  @Column(nullable = true)
  private Integer uploads=0;

  @Column(nullable = true, unique = true)
  private String email;

  @Column(nullable = true)
  private String name;

  @Column(nullable = true, unique = true)
  private String apiKey;

  private DBUser() {
  }

  private DBUser(long totalSpace, Role role) {
    this.totalSpace = totalSpace;
    this.role = role;
  }

  /**
   * Create a new user with specified role
   *
   * @throws IllegalArgumentException If {@code totalSpace < 0}
   */
  public static DBUser createUser(
      String email,
      String name,
      long totalSpace,
      String username, DBUser.Role role) {
    if (totalSpace < 0) {
      throw new IllegalArgumentException();
    }
    DBUser dbUser = new DBUser(totalSpace, role);
    String lowerCaseEmail = email.toLowerCase();
    dbUser.setEmail(lowerCaseEmail);

    if (name == null || name.isEmpty()) {
      name = eu.europa.circabc.eushare.utils.StringUtils.emailToGivenName(email);
    }
    dbUser.setName(name);
    dbUser.setTotalSpace(totalSpace);
    dbUser.setUsername(username);
    dbUser.setLastLogged(LocalDateTime.now());
    dbUser.setCreationDate(LocalDateTime.now());
    dbUser.setStatus(DBUser.Status.REGULAR);
    return dbUser;
  }

  public long getFreeSpace() {
    return this.totalSpace - this.getUsedSpace();
  }

  @Transient
  private long getUsedSpace() {
    return this.filesUploaded.stream()
        .filter(dbFile -> dbFile.getStatus().equals(DBFile.Status.AVAILABLE))
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
      if (upload.getStatus().equals(DBFile.Status.AVAILABLE)) {
        totalSize = totalSize + upload.getSize();
      }
    }
    userInfo.setUsedSpace(new BigDecimal(totalSize));
    userInfo.setId(this.getId());
    userInfo.setGivenName(this.getName());
    userInfo.setLoginUsername(this.getUsername());
    UserInfo.RoleEnum userInfoRole = EnumConverter.convert(this.getRole(), UserInfo.RoleEnum.class);
    userInfo.setRole(userInfoRole);

    UserInfo.StatusEnum userInfoStatus = EnumConverter.convert(this.getStatus(), UserInfo.StatusEnum.class);
    userInfo.setStatus(userInfoStatus);

    userInfo.isAdmin(this.role.equals(Role.ADMIN));
    userInfo.setEmail(this.email);
    return userInfo;
  }

  public UserSpace toUserSpace() {
    UserSpace userSpace = new UserSpace();
    userSpace.setTotalSpace(new BigDecimal(this.totalSpace));
    long totalSize = 0;
    for (DBFile upload : this.filesUploaded) {
      if (upload.getStatus().equals(DBFile.Status.AVAILABLE)) {
        totalSize = totalSize + upload.getSize();
      }
    }
    userSpace.setUsedSpace(new BigDecimal(totalSize));
    return userSpace;
  }

  public enum Role {
    EXTERNAL,
    TRUSTED_EXTERNAL,
    INTERNAL, // An active user. Has an email, a name and a username.
    ADMIN, // An internal user with extra advantage
    API_KEY
  }

  public enum Status {
    REGULAR,
    SUSPENDED,
    BANNED
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

  public LocalDateTime getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(LocalDateTime creationDate) {
    this.creationDate = creationDate;
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

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

}
