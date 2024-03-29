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
import eu.europa.circabc.eushare.model.FileBasics;
import eu.europa.circabc.eushare.model.FileRequestAllOf;
import eu.europa.circabc.eushare.model.Recipient;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

// eushare
import com.fasterxml.jackson.annotation.JsonFormat;
/**
 * FileRequest
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class FileRequest   {
  @JsonProperty("expirationDate")
  // Eushare
  @JsonFormat(pattern = "yyyy-MM-dd")
  
  @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
  private LocalDate expirationDate;

  @JsonProperty("hasPassword")
  private Boolean hasPassword;

  @JsonProperty("name")
  private String name;

  @JsonProperty("size")
  private BigDecimal size;

  @JsonProperty("password")
  private String password;

  @JsonProperty("downloadNotification")
  private Boolean downloadNotification;

  @JsonProperty("sharedWith")
  @Valid
  private List<Recipient> sharedWith = new ArrayList<>();

  public FileRequest expirationDate(LocalDate expirationDate) {
    this.expirationDate = expirationDate;
    return this;
  }

  /**
   * Expiration date of file
   * @return expirationDate
  */
  @ApiModelProperty(required = true, value = "Expiration date of file")
  @NotNull

  @Valid

  public LocalDate getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(LocalDate expirationDate) {
    this.expirationDate = expirationDate;
  }

  public FileRequest hasPassword(Boolean hasPassword) {
    this.hasPassword = hasPassword;
    return this;
  }

  /**
   * File is password-protected
   * @return hasPassword
  */
  @ApiModelProperty(required = true, value = "File is password-protected")
  @NotNull


  public Boolean getHasPassword() {
    return hasPassword;
  }

  public void setHasPassword(Boolean hasPassword) {
    this.hasPassword = hasPassword;
  }

  public FileRequest name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Filename
   * @return name
  */
  @ApiModelProperty(required = true, value = "Filename")
  @NotNull


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public FileRequest size(BigDecimal size) {
    this.size = size;
    return this;
  }

  /**
   * Size of file (Bytes)
   * minimum: 0
   * @return size
  */
  @ApiModelProperty(required = true, value = "Size of file (Bytes)")
  @NotNull

  @Valid
@DecimalMin("0")
  public BigDecimal getSize() {
    return size;
  }

  public void setSize(BigDecimal size) {
    this.size = size;
  }

  public FileRequest password(String password) {
    this.password = password;
    return this;
  }

  /**
   * Password protecting the file
   * @return password
  */
  @ApiModelProperty(value = "Password protecting the file")


  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public FileRequest downloadNotification(Boolean downloadNotification) {
    this.downloadNotification = downloadNotification;
    return this;
  }

  /**
   * Email notification sent after download if set to true
   * @return downloadNotification
  */
  @ApiModelProperty(value = "Email notification sent after download if set to true")


  public Boolean getDownloadNotification() {
    return downloadNotification;
  }

  public void setDownloadNotification(Boolean downloadNotification) {
    this.downloadNotification = downloadNotification;
  }

  public FileRequest sharedWith(List<Recipient> sharedWith) {
    this.sharedWith = sharedWith;
    return this;
  }

  public FileRequest addSharedWithItem(Recipient sharedWithItem) {
    this.sharedWith.add(sharedWithItem);
    return this;
  }

  /**
   * User IDs this file is shared with
   * @return sharedWith
  */
  @ApiModelProperty(required = true, value = "User IDs this file is shared with")
  @NotNull

  @Valid
@Size(min=1,max=10) 
  public List<Recipient> getSharedWith() {
    return sharedWith;
  }

  public void setSharedWith(List<Recipient> sharedWith) {
    this.sharedWith = sharedWith;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FileRequest fileRequest = (FileRequest) o;
    return Objects.equals(this.expirationDate, fileRequest.expirationDate) &&
        Objects.equals(this.hasPassword, fileRequest.hasPassword) &&
        Objects.equals(this.name, fileRequest.name) &&
        Objects.equals(this.size, fileRequest.size) &&
        Objects.equals(this.password, fileRequest.password) &&
        Objects.equals(this.downloadNotification, fileRequest.downloadNotification) &&
        Objects.equals(this.sharedWith, fileRequest.sharedWith);
  }

  @Override
  public int hashCode() {
    return Objects.hash(expirationDate, hasPassword, name, size, password, downloadNotification, sharedWith);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FileRequest {\n");
    
    sb.append("    expirationDate: ").append(toIndentedString(expirationDate)).append("\n");
    sb.append("    hasPassword: ").append(toIndentedString(hasPassword)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    password: ").append(toIndentedString(password)).append("\n");
    sb.append("    downloadNotification: ").append(toIndentedString(downloadNotification)).append("\n");
    sb.append("    sharedWith: ").append(toIndentedString(sharedWith)).append("\n");
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

