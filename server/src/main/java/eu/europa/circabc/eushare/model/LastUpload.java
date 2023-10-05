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
 * LastUpload
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class LastUpload   {
  @JsonProperty("uploader_email")
  private String uploaderEmail;

  @JsonProperty("share_email")
  private String shareEmail;

  @JsonProperty("filename")
  private String filename;

  @JsonProperty("file_size")
  private Long fileSize;

  @JsonProperty("path")
  private String path;

  @JsonProperty("status")
  private String status;

  @JsonProperty("created")
  // Eushare
  @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
  
  @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime created;

  @JsonProperty("shorturl")
  private String shorturl;

  @JsonProperty("download_notification")
  private Boolean downloadNotification;

  public LastUpload uploaderEmail(String uploaderEmail) {
    this.uploaderEmail = uploaderEmail;
    return this;
  }

  /**
   * Get uploaderEmail
   * @return uploaderEmail
  */
  @ApiModelProperty(value = "")


  public String getUploaderEmail() {
    return uploaderEmail;
  }

  public void setUploaderEmail(String uploaderEmail) {
    this.uploaderEmail = uploaderEmail;
  }

  public LastUpload shareEmail(String shareEmail) {
    this.shareEmail = shareEmail;
    return this;
  }

  /**
   * Get shareEmail
   * @return shareEmail
  */
  @ApiModelProperty(value = "")


  public String getShareEmail() {
    return shareEmail;
  }

  public void setShareEmail(String shareEmail) {
    this.shareEmail = shareEmail;
  }

  public LastUpload filename(String filename) {
    this.filename = filename;
    return this;
  }

  /**
   * Get filename
   * @return filename
  */
  @ApiModelProperty(value = "")


  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public LastUpload fileSize(Long fileSize) {
    this.fileSize = fileSize;
    return this;
  }

  /**
   * Get fileSize
   * @return fileSize
  */
  @ApiModelProperty(value = "")


  public Long getFileSize() {
    return fileSize;
  }

  public void setFileSize(Long fileSize) {
    this.fileSize = fileSize;
  }

  public LastUpload path(String path) {
    this.path = path;
    return this;
  }

  /**
   * Get path
   * @return path
  */
  @ApiModelProperty(value = "")


  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public LastUpload status(String status) {
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

  public LastUpload created(OffsetDateTime created) {
    this.created = created;
    return this;
  }

  /**
   * Get created
   * @return created
  */
  @ApiModelProperty(value = "")

  @Valid

  public OffsetDateTime getCreated() {
    return created;
  }

  public void setCreated(OffsetDateTime created) {
    this.created = created;
  }

  public LastUpload shorturl(String shorturl) {
    this.shorturl = shorturl;
    return this;
  }

  /**
   * Get shorturl
   * @return shorturl
  */
  @ApiModelProperty(value = "")


  public String getShorturl() {
    return shorturl;
  }

  public void setShorturl(String shorturl) {
    this.shorturl = shorturl;
  }

  public LastUpload downloadNotification(Boolean downloadNotification) {
    this.downloadNotification = downloadNotification;
    return this;
  }

  /**
   * Get downloadNotification
   * @return downloadNotification
  */
  @ApiModelProperty(value = "")


  public Boolean getDownloadNotification() {
    return downloadNotification;
  }

  public void setDownloadNotification(Boolean downloadNotification) {
    this.downloadNotification = downloadNotification;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LastUpload lastUpload = (LastUpload) o;
    return Objects.equals(this.uploaderEmail, lastUpload.uploaderEmail) &&
        Objects.equals(this.shareEmail, lastUpload.shareEmail) &&
        Objects.equals(this.filename, lastUpload.filename) &&
        Objects.equals(this.fileSize, lastUpload.fileSize) &&
        Objects.equals(this.path, lastUpload.path) &&
        Objects.equals(this.status, lastUpload.status) &&
        Objects.equals(this.created, lastUpload.created) &&
        Objects.equals(this.shorturl, lastUpload.shorturl) &&
        Objects.equals(this.downloadNotification, lastUpload.downloadNotification);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uploaderEmail, shareEmail, filename, fileSize, path, status, created, shorturl, downloadNotification);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LastUpload {\n");
    
    sb.append("    uploaderEmail: ").append(toIndentedString(uploaderEmail)).append("\n");
    sb.append("    shareEmail: ").append(toIndentedString(shareEmail)).append("\n");
    sb.append("    filename: ").append(toIndentedString(filename)).append("\n");
    sb.append("    fileSize: ").append(toIndentedString(fileSize)).append("\n");
    sb.append("    path: ").append(toIndentedString(path)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    created: ").append(toIndentedString(created)).append("\n");
    sb.append("    shorturl: ").append(toIndentedString(shorturl)).append("\n");
    sb.append("    downloadNotification: ").append(toIndentedString(downloadNotification)).append("\n");
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

