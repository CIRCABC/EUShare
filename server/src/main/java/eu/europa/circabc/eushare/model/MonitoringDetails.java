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
import eu.europa.circabc.eushare.model.Monitoring;
import eu.europa.circabc.eushare.model.MonitoringDetailsAllOf;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

// eushare
import com.fasterxml.jackson.annotation.JsonFormat;
/**
 * MonitoringDetails
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class MonitoringDetails   {
  @JsonProperty("ID")
  private String ID;

  @JsonProperty("datetime")
  private String datetime;

  /**
   * Gets or Sets event
   */
  public enum EventEnum {
    DOWNLOAD_RATE_HOUR("DOWNLOAD_RATE_HOUR"),
    
    UPLOAD_RATE_HOUR("UPLOAD_RATE_HOUR"),
    
    DOWNLOAD_RATE_DAY("DOWNLOAD_RATE_DAY"),
    
    UPLOAD_RATE_DAY("UPLOAD_RATE_DAY"),
    
    USER_CREATION_DAY("USER_CREATION_DAY");

    private String value;

    EventEnum(String value) {
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
    public static EventEnum fromValue(String value) {
      for (EventEnum b : EventEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("event")
  private EventEnum event;

  @JsonProperty("fileId")
  private String fileId;

  @JsonProperty("userId")
  private String userId;

  @JsonProperty("counter")
  private Long counter;

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

  @JsonProperty("description")
  private String description;

  public MonitoringDetails ID(String ID) {
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

  public MonitoringDetails datetime(String datetime) {
    this.datetime = datetime;
    return this;
  }

  /**
   * Get datetime
   * @return datetime
  */
  @ApiModelProperty(value = "")


  public String getDatetime() {
    return datetime;
  }

  public void setDatetime(String datetime) {
    this.datetime = datetime;
  }

  public MonitoringDetails event(EventEnum event) {
    this.event = event;
    return this;
  }

  /**
   * Get event
   * @return event
  */
  @ApiModelProperty(value = "")


  public EventEnum getEvent() {
    return event;
  }

  public void setEvent(EventEnum event) {
    this.event = event;
  }

  public MonitoringDetails fileId(String fileId) {
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

  public MonitoringDetails userId(String userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Get userId
   * @return userId
  */
  @ApiModelProperty(value = "")


  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public MonitoringDetails counter(Long counter) {
    this.counter = counter;
    return this;
  }

  /**
   * Get counter
   * @return counter
  */
  @ApiModelProperty(value = "")


  public Long getCounter() {
    return counter;
  }

  public void setCounter(Long counter) {
    this.counter = counter;
  }

  public MonitoringDetails status(StatusEnum status) {
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

  public MonitoringDetails filename(String filename) {
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

  public MonitoringDetails filesize(BigDecimal filesize) {
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

  public MonitoringDetails shortUrl(String shortUrl) {
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

  public MonitoringDetails uploaderEmail(String uploaderEmail) {
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

  public MonitoringDetails uploaderName(String uploaderName) {
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

  public MonitoringDetails uploaderStatus(String uploaderStatus) {
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

  public MonitoringDetails description(String description) {
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
    MonitoringDetails monitoringDetails = (MonitoringDetails) o;
    return Objects.equals(this.ID, monitoringDetails.ID) &&
        Objects.equals(this.datetime, monitoringDetails.datetime) &&
        Objects.equals(this.event, monitoringDetails.event) &&
        Objects.equals(this.fileId, monitoringDetails.fileId) &&
        Objects.equals(this.userId, monitoringDetails.userId) &&
        Objects.equals(this.counter, monitoringDetails.counter) &&
        Objects.equals(this.status, monitoringDetails.status) &&
        Objects.equals(this.filename, monitoringDetails.filename) &&
        Objects.equals(this.filesize, monitoringDetails.filesize) &&
        Objects.equals(this.shortUrl, monitoringDetails.shortUrl) &&
        Objects.equals(this.uploaderEmail, monitoringDetails.uploaderEmail) &&
        Objects.equals(this.uploaderName, monitoringDetails.uploaderName) &&
        Objects.equals(this.uploaderStatus, monitoringDetails.uploaderStatus) &&
        Objects.equals(this.description, monitoringDetails.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ID, datetime, event, fileId, userId, counter, status, filename, filesize, shortUrl, uploaderEmail, uploaderName, uploaderStatus, description);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MonitoringDetails {\n");
    
    sb.append("    ID: ").append(toIndentedString(ID)).append("\n");
    sb.append("    datetime: ").append(toIndentedString(datetime)).append("\n");
    sb.append("    event: ").append(toIndentedString(event)).append("\n");
    sb.append("    fileId: ").append(toIndentedString(fileId)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    counter: ").append(toIndentedString(counter)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
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

