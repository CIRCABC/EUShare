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

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import eu.europa.circabc.eushare.model.TrustRequest;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.OffsetDateTime;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.*;

// eushare
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table( name = "Trust")
@ApiModel(description = "DBTrust")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class DBTrust {
  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @JsonProperty("id")
  private String id;

  @JsonProperty("description")
  private String description;

  @JsonProperty("email")
  private String email;

  @JsonProperty("approved")
  private Boolean approved;

  @JsonProperty("requestDateTime")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime requestDateTime;

  @JsonProperty("name")
  private String name;

  @JsonProperty("username")
  private String username;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Boolean getApproved() {
    return approved;
  }

  public void setApproved(Boolean approved) {
    this.approved = approved;
  }

  public OffsetDateTime getRequestDateTime() {
    return requestDateTime;
  }

  public void setRequestDateTime(OffsetDateTime requestDateTime) {
    this.requestDateTime = requestDateTime;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DBTrust dbTrust = (DBTrust) o;
    return Objects.equals(this.id, dbTrust.id) &&
        Objects.equals(this.description, dbTrust.description) &&
        Objects.equals(this.email, dbTrust.email) &&
        Objects.equals(this.approved, dbTrust.approved) &&
        Objects.equals(this.requestDateTime, dbTrust.requestDateTime) &&
        Objects.equals(this.name, dbTrust.name) &&
        Objects.equals(this.username, dbTrust.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, description, email, approved, requestDateTime, name, username);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DBTrust {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    approved: ").append(toIndentedString(approved)).append("\n");
    sb.append("    requestDateTime: ").append(toIndentedString(requestDateTime)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

  public TrustRequest toTrustRequest() {
    TrustRequest trustRequest = new TrustRequest();
    trustRequest.setId(this.getId());
    trustRequest.setDescription(this.getDescription());
    trustRequest.setEmail(this.getEmail());
    trustRequest.setApproved(this.getApproved());
    trustRequest.setRequestDateTime(this.getRequestDateTime());
    trustRequest.setName(this.getName());
    trustRequest.setUsername(this.getUsername());
    return trustRequest;
  }

  public static DBTrust fromTrustRequest(TrustRequest trustRequest) {
    DBTrust dbTrust = new DBTrust();
    dbTrust.setId(trustRequest.getId());
    dbTrust.setDescription(trustRequest.getDescription());
    dbTrust.setEmail(trustRequest.getEmail());
    dbTrust.setApproved(trustRequest.getApproved());
    dbTrust.setRequestDateTime(trustRequest.getRequestDateTime());
    dbTrust.setName(trustRequest.getName());
    dbTrust.setUsername(trustRequest.getUsername());
    return dbTrust;
  }
}
