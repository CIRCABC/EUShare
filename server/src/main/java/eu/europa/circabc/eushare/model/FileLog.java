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
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

// eushare
import com.fasterxml.jackson.annotation.JsonFormat;
/**
 * FileLog
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class FileLog   {
  @JsonProperty("fileId")
  private String fileId;

  @JsonProperty("downloadDate")
  private String downloadDate;

  @JsonProperty("recipient")
  private String recipient;

  @JsonProperty("downloadLink")
  private String downloadLink;

  public FileLog fileId(String fileId) {
    this.fileId = fileId;
    return this;
  }

  /**
   * ID of the file
   * @return fileId
  */
  @ApiModelProperty(required = true, value = "ID of the file")
  @NotNull


  public String getFileId() {
    return fileId;
  }

  public void setFileId(String fileId) {
    this.fileId = fileId;
  }

  public FileLog downloadDate(String downloadDate) {
    this.downloadDate = downloadDate;
    return this;
  }

  /**
   * Download date of file
   * @return downloadDate
  */
  @ApiModelProperty(required = true, value = "Download date of file")
  @NotNull


  public String getDownloadDate() {
    return downloadDate;
  }

  public void setDownloadDate(String downloadDate) {
    this.downloadDate = downloadDate;
  }

  public FileLog recipient(String recipient) {
    this.recipient = recipient;
    return this;
  }

  /**
   * recipient of the file
   * @return recipient
  */
  @ApiModelProperty(required = true, value = "recipient of the file")
  @NotNull


  public String getRecipient() {
    return recipient;
  }

  public void setRecipient(String recipient) {
    this.recipient = recipient;
  }

  public FileLog downloadLink(String downloadLink) {
    this.downloadLink = downloadLink;
    return this;
  }

  /**
   * download link
   * @return downloadLink
  */
  @ApiModelProperty(required = true, value = "download link")
  @NotNull


  public String getDownloadLink() {
    return downloadLink;
  }

  public void setDownloadLink(String downloadLink) {
    this.downloadLink = downloadLink;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FileLog fileLog = (FileLog) o;
    return Objects.equals(this.fileId, fileLog.fileId) &&
        Objects.equals(this.downloadDate, fileLog.downloadDate) &&
        Objects.equals(this.recipient, fileLog.recipient) &&
        Objects.equals(this.downloadLink, fileLog.downloadLink);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fileId, downloadDate, recipient, downloadLink);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FileLog {\n");
    
    sb.append("    fileId: ").append(toIndentedString(fileId)).append("\n");
    sb.append("    downloadDate: ").append(toIndentedString(downloadDate)).append("\n");
    sb.append("    recipient: ").append(toIndentedString(recipient)).append("\n");
    sb.append("    downloadLink: ").append(toIndentedString(downloadLink)).append("\n");
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

