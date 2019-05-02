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
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * FileInfoRecipient
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-04-10T14:56:31.271+02:00[Europe/Paris]")

public class FileInfoRecipient   {
  @JsonProperty("expirationDate")
  private LocalDate expirationDate;

  @JsonProperty("hasPassword")
  private Boolean hasPassword;

  @JsonProperty("name")
  private String name;

  @JsonProperty("size")
  private BigDecimal size;

  @JsonProperty("uploader")
  private String uploader;

  public FileInfoRecipient expirationDate(LocalDate expirationDate) {
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

  public FileInfoRecipient hasPassword(Boolean hasPassword) {
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

  public FileInfoRecipient name(String name) {
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

  public FileInfoRecipient size(BigDecimal size) {
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

  public FileInfoRecipient uploader(String uploader) {
    this.uploader = uploader;
    return this;
  }

  /**
   * User ID
   * @return uploader
  */
  @ApiModelProperty(required = true, value = "User ID")
  @NotNull


  public String getUploader() {
    return uploader;
  }

  public void setUploader(String uploader) {
    this.uploader = uploader;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FileInfoRecipient fileInfoRecipient = (FileInfoRecipient) o;
    return Objects.equals(this.expirationDate, fileInfoRecipient.expirationDate) &&
        Objects.equals(this.hasPassword, fileInfoRecipient.hasPassword) &&
        Objects.equals(this.name, fileInfoRecipient.name) &&
        Objects.equals(this.size, fileInfoRecipient.size) &&
        Objects.equals(this.uploader, fileInfoRecipient.uploader);
  }

  @Override
  public int hashCode() {
    return Objects.hash(expirationDate, hasPassword, name, size, uploader);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FileInfoRecipient {\n");
    
    sb.append("    expirationDate: ").append(toIndentedString(expirationDate)).append("\n");
    sb.append("    hasPassword: ").append(toIndentedString(hasPassword)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    uploader: ").append(toIndentedString(uploader)).append("\n");
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

