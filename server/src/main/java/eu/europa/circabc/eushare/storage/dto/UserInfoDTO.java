/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.storage.dto;

import eu.europa.circabc.eushare.model.EnumConverter;
import eu.europa.circabc.eushare.model.UserInfo;
import eu.europa.circabc.eushare.model.UserSpace;
import eu.europa.circabc.eushare.services.UserService;
import eu.europa.circabc.eushare.storage.entity.DBUser.Role;
import eu.europa.circabc.eushare.storage.entity.DBUser.Status;

import java.math.BigDecimal;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

public class UserInfoDTO {

  private static final long serialVersionUID = 1L;

  private String id;

  @Enumerated(value = EnumType.STRING)
  private Role role;

  @Enumerated(value = EnumType.STRING)
  private Status status;

  private String username;
  private String email;
  private String name;
  private long totalSpace;
  private long usedSpace;

  private long filesCount;

  private UserInfoDTO() {
  }

  public UserInfoDTO(String id, String roleStr, String statusStr, String username, String name,String email, 
      long totalSpace, long usedSpace, long filesCount) {
    this.id = id;
    this.role = Role.valueOf(roleStr);
    this.status = Status.valueOf(statusStr);
    this.username = username;
    this.email = email;
    this.name = name;
    this.totalSpace = totalSpace;
    this.usedSpace = usedSpace;
    this.filesCount = filesCount;
  }

  public UserInfo toUserInfo() {
    UserInfo userInfo = new UserInfo();
    userInfo.setTotalSpace(new BigDecimal(this.totalSpace));
    userInfo.setUsedSpace(new BigDecimal(this.usedSpace));
    userInfo.setFilesCount(new BigDecimal(this.filesCount));
    userInfo.setId(this.getId());

    UserInfo.RoleEnum userInfoRole = EnumConverter.convert(this.role, UserInfo.RoleEnum.class);
    userInfo.setRole(userInfoRole);

    UserInfo.StatusEnum userInfoStatus = EnumConverter.convert(this.status, UserInfo.StatusEnum.class);
    userInfo.setStatus(userInfoStatus);

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

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }
}
