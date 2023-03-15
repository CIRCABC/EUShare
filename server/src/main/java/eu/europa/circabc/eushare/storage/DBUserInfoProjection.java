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
import eu.europa.circabc.eushare.storage.DBUser.Role;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

// Use JPA projection : https://github.com/spring-projects/spring-data-examples/tree/main/jpa/jpa21/src/main/java/example/springdata/jpa/resultsetmappings

@Entity
public class DBUserInfoProjection {

  @Id
  private String id;

  @Enumerated(value = EnumType.STRING)
  private Role role;

  private String username;
  private String email;
  private String name;
  private long totalSpace;
  private long usedSpace;

  private long filesCount;

  private DBUserInfoProjection() {
  }

  /**
   * Convert to {@link UserInfo} object
   */
  public UserInfo toUserInfo() {
    UserInfo userInfo = new UserInfo();
    userInfo.setTotalSpace(new BigDecimal(this.totalSpace));
    userInfo.setUsedSpace(new BigDecimal(this.usedSpace));
    userInfo.setFilesCount(new BigDecimal(this.filesCount));
    userInfo.setId(this.getId());
    if (this.role.equals(Role.ADMIN))
      userInfo.setRole(UserInfo.RoleEnum.ADMIN);
    if (this.role.equals(Role.INTERNAL))
      userInfo.setRole(UserInfo.RoleEnum.INTERNAL);
    if (this.role.equals(Role.API_KEY))
      userInfo.setRole(UserInfo.RoleEnum.API_KEY);

    userInfo.setGivenName(this.getName());
    userInfo.setLoginUsername(this.getUsername());
    userInfo.isAdmin(this.role.equals(Role.ADMIN));
    userInfo.setEmail(this.email);
    return userInfo;
  }

  public UserSpace toUserSpace() {
    UserSpace userSpace = new UserSpace();
    userSpace.setTotalSpace(new BigDecimal(this.totalSpace));
    userSpace.setUsedSpace(new BigDecimal(this.usedSpace));
    return userSpace;
  }

  public long getFilesCount() {
    return filesCount;
  }

  public void setFilesCount(long filesCount) {
    this.filesCount = filesCount;
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

  public long getTotalSpace() {
    return totalSpace;
  }

  public void setTotalSpace(long totalSpace) {
    this.totalSpace = totalSpace;
  }

  public long getUsedSpace() {
    return usedSpace;
  }

  public void setUsedSpace(long usedSpace) {
    this.usedSpace = usedSpace;
  }
}
