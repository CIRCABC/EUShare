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
import java.math.BigDecimal;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

// eushare
import com.fasterxml.jackson.annotation.JsonFormat;
/**
 * MonitoringDetailsAllOf
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class MonitoringDetailsAllOf   {
  @JsonProperty("filename")
  private String filename;

  @JsonProperty("filesize")
  private BigDecimal filesize;

  @JsonProperty("shortUrl")
  private String shortUrl;

  @JsonProperty("uploader_email")
  private String uploaderEmail;

  @JsonProperty("uploader_name")
  private String uploaderName;

  @JsonProperty("uploader_status")
  private String uploaderStatus;

  @JsonProperty("description")
  private String description;

  public MonitoringDetailsAllOf filename(String filename) {
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

  public MonitoringDetailsAllOf filesize(BigDecimal filesize) {
    this.filesize = filesize;
    return this;
  }

  /**
   * Get filesize
   * @return filesize
  */
  @ApiModelProperty(value = "")

  @Valid

  public BigDecimal getFilesize() {
    return filesize;
  }

  public void setFilesize(BigDecimal filesize) {
    this.filesize = filesize;
  }

  public MonitoringDetailsAllOf shortUrl(String shortUrl) {
    this.shortUrl = shortUrl;
    return this;
  }

  /**
   * Get shortUrl
   * @return shortUrl
  */
  @ApiModelProperty(value = "")


  public String getShortUrl() {
    return shortUrl;
  }

  public void setShortUrl(String shortUrl) {
    this.shortUrl = shortUrl;
  }

  public MonitoringDetailsAllOf uploaderEmail(String uploaderEmail) {
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

  public MonitoringDetailsAllOf uploaderName(String uploaderName) {
    this.uploaderName = uploaderName;
    return this;
  }

  /**
   * Get uploaderName
   * @return uploaderName
  */
  @ApiModelProperty(value = "")


  public String getUploaderName() {
    return uploaderName;
  }

  public void setUploaderName(String uploaderName) {
    this.uploaderName = uploaderName;
  }

  public MonitoringDetailsAllOf uploaderStatus(String uploaderStatus) {
    this.uploaderStatus = uploaderStatus;
    return this;
  }

  /**
   * Get uploaderStatus
   * @return uploaderStatus
  */
  @ApiModelProperty(value = "")


  public String getUploaderStatus() {
    return uploaderStatus;
  }

  public void setUploaderStatus(String uploaderStatus) {
    this.uploaderStatus = uploaderStatus;
  }

  public MonitoringDetailsAllOf description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
  */
  @ApiModelProperty(value = "")


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MonitoringDetailsAllOf monitoringDetailsAllOf = (MonitoringDetailsAllOf) o;
    return Objects.equals(this.filename, monitoringDetailsAllOf.filename) &&
        Objects.equals(this.filesize, monitoringDetailsAllOf.filesize) &&
        Objects.equals(this.shortUrl, monitoringDetailsAllOf.shortUrl) &&
        Objects.equals(this.uploaderEmail, monitoringDetailsAllOf.uploaderEmail) &&
        Objects.equals(this.uploaderName, monitoringDetailsAllOf.uploaderName) &&
        Objects.equals(this.uploaderStatus, monitoringDetailsAllOf.uploaderStatus) &&
        Objects.equals(this.description, monitoringDetailsAllOf.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(filename, filesize, shortUrl, uploaderEmail, uploaderName, uploaderStatus, description);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MonitoringDetailsAllOf {\n");
    
    sb.append("    filename: ").append(toIndentedString(filename)).append("\n");
    sb.append("    filesize: ").append(toIndentedString(filesize)).append("\n");
    sb.append("    shortUrl: ").append(toIndentedString(shortUrl)).append("\n");
    sb.append("    uploaderEmail: ").append(toIndentedString(uploaderEmail)).append("\n");
    sb.append("    uploaderName: ").append(toIndentedString(uploaderName)).append("\n");
    sb.append("    uploaderStatus: ").append(toIndentedString(uploaderStatus)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
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

