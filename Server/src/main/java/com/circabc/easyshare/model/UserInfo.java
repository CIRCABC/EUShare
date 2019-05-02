/**
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */

package com.circabc.easyshare.model;

import java.util.Objects;
import com.circabc.easyshare.model.UserSpace;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * UserInfo
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-04-10T14:56:31.271+02:00[Europe/Paris]")

public class UserInfo   {
  @JsonProperty("totalSpace")
  private BigDecimal totalSpace;

  @JsonProperty("usedSpace")
  private BigDecimal usedSpace;

  @JsonProperty("id")
  private String id;

  @JsonProperty("uploadedFiles")
  @Valid
  private List<String> uploadedFiles = new ArrayList<>();

  @JsonProperty("sharedFiles")
  @Valid
  private List<String> sharedFiles = new ArrayList<>();

  @JsonProperty("isAdmin")
  private Boolean isAdmin;

  public UserInfo totalSpace(BigDecimal totalSpace) {
    this.totalSpace = totalSpace;
    return this;
  }

  /**
   * Total space the user has (Bytes)
   * minimum: 0
   * @return totalSpace
  */
  @ApiModelProperty(required = true, value = "Total space the user has (Bytes)")
  @NotNull

  @Valid
@DecimalMin("0")
  public BigDecimal getTotalSpace() {
    return totalSpace;
  }

  public void setTotalSpace(BigDecimal totalSpace) {
    this.totalSpace = totalSpace;
  }

  public UserInfo usedSpace(BigDecimal usedSpace) {
    this.usedSpace = usedSpace;
    return this;
  }

  /**
   * Space the user already used (Bytes)
   * minimum: 0
   * @return usedSpace
  */
  @ApiModelProperty(required = true, value = "Space the user already used (Bytes)")
  @NotNull

  @Valid
@DecimalMin("0")
  public BigDecimal getUsedSpace() {
    return usedSpace;
  }

  public void setUsedSpace(BigDecimal usedSpace) {
    this.usedSpace = usedSpace;
  }

  public UserInfo id(String id) {
    this.id = id;
    return this;
  }

  /**
   * User ID (email address in case of external user)
   * @return id
  */
  @ApiModelProperty(required = true, value = "User ID (email address in case of external user)")
  @NotNull


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public UserInfo uploadedFiles(List<String> uploadedFiles) {
    this.uploadedFiles = uploadedFiles;
    return this;
  }

  public UserInfo addUploadedFilesItem(String uploadedFilesItem) {
    this.uploadedFiles.add(uploadedFilesItem);
    return this;
  }

  /**
   * Files the user has uploaded
   * @return uploadedFiles
  */
  @ApiModelProperty(required = true, value = "Files the user has uploaded")
  @NotNull


  public List<String> getUploadedFiles() {
    return uploadedFiles;
  }

  public void setUploadedFiles(List<String> uploadedFiles) {
    this.uploadedFiles = uploadedFiles;
  }

  public UserInfo sharedFiles(List<String> sharedFiles) {
    this.sharedFiles = sharedFiles;
    return this;
  }

  public UserInfo addSharedFilesItem(String sharedFilesItem) {
    this.sharedFiles.add(sharedFilesItem);
    return this;
  }

  /**
   * Files the user has given access to
   * @return sharedFiles
  */
  @ApiModelProperty(required = true, value = "Files the user has given access to")
  @NotNull


  public List<String> getSharedFiles() {
    return sharedFiles;
  }

  public void setSharedFiles(List<String> sharedFiles) {
    this.sharedFiles = sharedFiles;
  }

  public UserInfo isAdmin(Boolean isAdmin) {
    this.isAdmin = isAdmin;
    return this;
  }

  /**
   * True if the user is admin
   * @return isAdmin
  */
  @ApiModelProperty(required = true, value = "True if the user is admin")
  @NotNull


  public Boolean getIsAdmin() {
    return isAdmin;
  }

  public void setIsAdmin(Boolean isAdmin) {
    this.isAdmin = isAdmin;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserInfo userInfo = (UserInfo) o;
    return Objects.equals(this.totalSpace, userInfo.totalSpace) &&
        Objects.equals(this.usedSpace, userInfo.usedSpace) &&
        Objects.equals(this.id, userInfo.id) &&
        Objects.equals(this.uploadedFiles, userInfo.uploadedFiles) &&
        Objects.equals(this.sharedFiles, userInfo.sharedFiles) &&
        Objects.equals(this.isAdmin, userInfo.isAdmin);
  }

  @Override
  public int hashCode() {
    return Objects.hash(totalSpace, usedSpace, id, uploadedFiles, sharedFiles, isAdmin);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserInfo {\n");
    
    sb.append("    totalSpace: ").append(toIndentedString(totalSpace)).append("\n");
    sb.append("    usedSpace: ").append(toIndentedString(usedSpace)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    uploadedFiles: ").append(toIndentedString(uploadedFiles)).append("\n");
    sb.append("    sharedFiles: ").append(toIndentedString(sharedFiles)).append("\n");
    sb.append("    isAdmin: ").append(toIndentedString(isAdmin)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

