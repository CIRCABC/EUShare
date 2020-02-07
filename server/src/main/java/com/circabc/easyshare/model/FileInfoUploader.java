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
import com.circabc.easyshare.model.RecipientWithLink;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * FileInfoUploader
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-11-05T16:07:50.538+01:00[Europe/Paris]")

public class FileInfoUploader   {
  @JsonProperty("expirationDate")
  @JsonFormat(pattern="yyyy-MM-dd")
  private LocalDate expirationDate;

  @JsonProperty("hasPassword")
  private Boolean hasPassword;

  @JsonProperty("name")
  private String name;

  @JsonProperty("size")
  private BigDecimal size;

  @JsonProperty("fileId")
  private String fileId;

  @JsonProperty("sharedWith")
  @Valid
  private List<RecipientWithLink> sharedWith = new ArrayList<>();

  public FileInfoUploader expirationDate(LocalDate expirationDate) {
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

  public FileInfoUploader hasPassword(Boolean hasPassword) {
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

  public FileInfoUploader name(String name) {
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

  public FileInfoUploader size(BigDecimal size) {
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

  public FileInfoUploader fileId(String fileId) {
    this.fileId = fileId;
    return this;
  }

  /**
   * file id
   * @return fileId
  */
  @ApiModelProperty(required = true, value = "file id")
  @NotNull


  public String getFileId() {
    return fileId;
  }

  public void setFileId(String fileId) {
    this.fileId = fileId;
  }

  public FileInfoUploader sharedWith(List<RecipientWithLink> sharedWith) {
    this.sharedWith = sharedWith;
    return this;
  }

  public FileInfoUploader addSharedWithItem(RecipientWithLink sharedWithItem) {
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
  public List<RecipientWithLink> getSharedWith() {
    return sharedWith;
  }

  public void setSharedWith(List<RecipientWithLink> sharedWith) {
    this.sharedWith = sharedWith;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FileInfoUploader fileInfoUploader = (FileInfoUploader) o;
    return Objects.equals(this.expirationDate, fileInfoUploader.expirationDate) &&//NOSONAR
        Objects.equals(this.hasPassword, fileInfoUploader.hasPassword) &&
        Objects.equals(this.name, fileInfoUploader.name) &&
        Objects.equals(this.size, fileInfoUploader.size) &&
        Objects.equals(this.fileId, fileInfoUploader.fileId) &&
        Objects.equals(this.sharedWith, fileInfoUploader.sharedWith);
  }

  @Override
  public int hashCode() {
    return Objects.hash(expirationDate, hasPassword, name, size, fileId, sharedWith);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FileInfoUploader {\n");
    
    sb.append("    expirationDate: ").append(toIndentedString(expirationDate)).append("\n");
    sb.append("    hasPassword: ").append(toIndentedString(hasPassword)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    fileId: ").append(toIndentedString(fileId)).append("\n");
    sb.append("    sharedWith: ").append(toIndentedString(sharedWith)).append("\n");
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

