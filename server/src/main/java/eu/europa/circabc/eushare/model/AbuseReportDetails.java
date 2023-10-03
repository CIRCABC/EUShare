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
import com.fasterxml.jackson.annotation.JsonValue;
import eu.europa.circabc.eushare.model.AbuseReport;
import eu.europa.circabc.eushare.model.AbuseReportDetailsAllOf;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

// eushare
import com.fasterxml.jackson.annotation.JsonFormat;
/**
 * AbuseReportDetails
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class AbuseReportDetails   {
  @JsonProperty("ID")
  private String ID;

  @JsonProperty("reporter")
  private String reporter;

  @JsonProperty("fileId")
  private String fileId;

  @JsonProperty("reason")
  private String reason;

  @JsonProperty("description")
  private String description;

  @JsonProperty("date")
  // Eushare
  @JsonFormat(pattern = "yyyy-MM-dd")
  
  @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
  private LocalDate date;

  /**
   * Gets or Sets status
   */
  public enum StatusEnum {
    WAITING("WAITING"),
    
    DENIED("DENIED"),
    
    APPROVED("APPROVED");

    private String value;

    StatusEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static StatusEnum fromValue(String value) {
      for (StatusEnum b : StatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("status")
  private StatusEnum status;

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

  public AbuseReportDetails ID(String ID) {
    this.ID = ID;
    return this;
  }

  /**
   * Get ID
   * @return ID
  */
  @ApiModelProperty(value = "")


  public String getID() {
    return ID;
  }

  public void setID(String ID) {
    this.ID = ID;
  }

  public AbuseReportDetails reporter(String reporter) {
    this.reporter = reporter;
    return this;
  }

  /**
   * Get reporter
   * @return reporter
  */
  @ApiModelProperty(value = "")


  public String getReporter() {
    return reporter;
  }

  public void setReporter(String reporter) {
    this.reporter = reporter;
  }

  public AbuseReportDetails fileId(String fileId) {
    this.fileId = fileId;
    return this;
  }

  /**
   * Get fileId
   * @return fileId
  */
  @ApiModelProperty(value = "")


  public String getFileId() {
    return fileId;
  }

  public void setFileId(String fileId) {
    this.fileId = fileId;
  }

  public AbuseReportDetails reason(String reason) {
    this.reason = reason;
    return this;
  }

  /**
   * Get reason
   * @return reason
  */
  @ApiModelProperty(value = "")


  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public AbuseReportDetails description(String description) {
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

  public AbuseReportDetails date(LocalDate date) {
    this.date = date;
    return this;
  }

  /**
   * Get date
   * @return date
  */
  @ApiModelProperty(value = "")

  @Valid

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public AbuseReportDetails status(StatusEnum status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
  */
  @ApiModelProperty(value = "")


  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  public AbuseReportDetails filename(String filename) {
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

  public AbuseReportDetails filesize(BigDecimal filesize) {
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

  public AbuseReportDetails shortUrl(String shortUrl) {
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

  public AbuseReportDetails uploaderEmail(String uploaderEmail) {
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

  public AbuseReportDetails uploaderName(String uploaderName) {
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

  public AbuseReportDetails uploaderStatus(String uploaderStatus) {
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


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AbuseReportDetails abuseReportDetails = (AbuseReportDetails) o;
    return Objects.equals(this.ID, abuseReportDetails.ID) &&
        Objects.equals(this.reporter, abuseReportDetails.reporter) &&
        Objects.equals(this.fileId, abuseReportDetails.fileId) &&
        Objects.equals(this.reason, abuseReportDetails.reason) &&
        Objects.equals(this.description, abuseReportDetails.description) &&
        Objects.equals(this.date, abuseReportDetails.date) &&
        Objects.equals(this.status, abuseReportDetails.status) &&
        Objects.equals(this.filename, abuseReportDetails.filename) &&
        Objects.equals(this.filesize, abuseReportDetails.filesize) &&
        Objects.equals(this.shortUrl, abuseReportDetails.shortUrl) &&
        Objects.equals(this.uploaderEmail, abuseReportDetails.uploaderEmail) &&
        Objects.equals(this.uploaderName, abuseReportDetails.uploaderName) &&
        Objects.equals(this.uploaderStatus, abuseReportDetails.uploaderStatus);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ID, reporter, fileId, reason, description, date, status, filename, filesize, shortUrl, uploaderEmail, uploaderName, uploaderStatus);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AbuseReportDetails {\n");
    
    sb.append("    ID: ").append(toIndentedString(ID)).append("\n");
    sb.append("    reporter: ").append(toIndentedString(reporter)).append("\n");
    sb.append("    fileId: ").append(toIndentedString(fileId)).append("\n");
    sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    date: ").append(toIndentedString(date)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    filename: ").append(toIndentedString(filename)).append("\n");
    sb.append("    filesize: ").append(toIndentedString(filesize)).append("\n");
    sb.append("    shortUrl: ").append(toIndentedString(shortUrl)).append("\n");
    sb.append("    uploaderEmail: ").append(toIndentedString(uploaderEmail)).append("\n");
    sb.append("    uploaderName: ").append(toIndentedString(uploaderName)).append("\n");
    sb.append("    uploaderStatus: ").append(toIndentedString(uploaderStatus)).append("\n");
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

