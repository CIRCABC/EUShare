/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.OffsetDateTime;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

// eushare
import com.fasterxml.jackson.annotation.JsonFormat;
/**
 * LastLogin
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class LastLogin   {
  @JsonProperty("id")
  private String id;

  @JsonProperty("email")
  private String email;

  @JsonProperty("name")
  private String name;

  @JsonProperty("username")
  private String username;

  @JsonProperty("total_space")
  private Long totalSpace;

  @JsonProperty("last_logged")
  // Eushare
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  
  @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime lastLogged;

  @JsonProperty("creation_date")
  // Eushare
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  
  @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime creationDate;

  @JsonProperty("uploads")
  private Integer uploads;

  @JsonProperty("status")
  private String status;

  public LastLogin id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  @ApiModelProperty(value = "")


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public LastLogin email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Get email
   * @return email
  */
  @ApiModelProperty(value = "")


  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public LastLogin name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
  */
  @ApiModelProperty(value = "")


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public LastLogin username(String username) {
    this.username = username;
    return this;
  }

  /**
   * Get username
   * @return username
  */
  @ApiModelProperty(value = "")


  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public LastLogin totalSpace(Long totalSpace) {
    this.totalSpace = totalSpace;
    return this;
  }

  /**
   * Get totalSpace
   * @return totalSpace
  */
  @ApiModelProperty(value = "")


  public Long getTotalSpace() {
    return totalSpace;
  }

  public void setTotalSpace(Long totalSpace) {
    this.totalSpace = totalSpace;
  }

  public LastLogin lastLogged(OffsetDateTime lastLogged) {
    this.lastLogged = lastLogged;
    return this;
  }

  /**
   * Get lastLogged
   * @return lastLogged
  */
  @ApiModelProperty(value = "")

  @Valid

  public OffsetDateTime getLastLogged() {
    return lastLogged;
  }

  public void setLastLogged(OffsetDateTime lastLogged) {
    this.lastLogged = lastLogged;
  }

  public LastLogin creationDate(OffsetDateTime creationDate) {
    this.creationDate = creationDate;
    return this;
  }

  /**
   * Get creationDate
   * @return creationDate
  */
  @ApiModelProperty(value = "")

  @Valid

  public OffsetDateTime getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(OffsetDateTime creationDate) {
    this.creationDate = creationDate;
  }

  public LastLogin uploads(Integer uploads) {
    this.uploads = uploads;
    return this;
  }

  /**
   * Get uploads
   * @return uploads
  */
  @ApiModelProperty(value = "")


  public Integer getUploads() {
    return uploads;
  }

  public void setUploads(Integer uploads) {
    this.uploads = uploads;
  }

  public LastLogin status(String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
  */
  @ApiModelProperty(value = "")


  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LastLogin lastLogin = (LastLogin) o;
    return Objects.equals(this.id, lastLogin.id) &&
        Objects.equals(this.email, lastLogin.email) &&
        Objects.equals(this.name, lastLogin.name) &&
        Objects.equals(this.username, lastLogin.username) &&
        Objects.equals(this.totalSpace, lastLogin.totalSpace) &&
        Objects.equals(this.lastLogged, lastLogin.lastLogged) &&
        Objects.equals(this.creationDate, lastLogin.creationDate) &&
        Objects.equals(this.uploads, lastLogin.uploads) &&
        Objects.equals(this.status, lastLogin.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, email, name, username, totalSpace, lastLogged, creationDate, uploads, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LastLogin {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    totalSpace: ").append(toIndentedString(totalSpace)).append("\n");
    sb.append("    lastLogged: ").append(toIndentedString(lastLogged)).append("\n");
    sb.append("    creationDate: ").append(toIndentedString(creationDate)).append("\n");
    sb.append("    uploads: ").append(toIndentedString(uploads)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

