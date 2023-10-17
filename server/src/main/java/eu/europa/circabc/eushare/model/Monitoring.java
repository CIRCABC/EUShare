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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

// eushare
import com.fasterxml.jackson.annotation.JsonFormat;
/**
 * Monitoring
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class Monitoring   {
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

  public Monitoring ID(String ID) {
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

  public Monitoring datetime(String datetime) {
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

  public Monitoring event(EventEnum event) {
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

  public Monitoring fileId(String fileId) {
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

  public Monitoring userId(String userId) {
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

  public Monitoring counter(Long counter) {
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

  public Monitoring status(StatusEnum status) {
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


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Monitoring monitoring = (Monitoring) o;
    return Objects.equals(this.ID, monitoring.ID) &&
        Objects.equals(this.datetime, monitoring.datetime) &&
        Objects.equals(this.event, monitoring.event) &&
        Objects.equals(this.fileId, monitoring.fileId) &&
        Objects.equals(this.userId, monitoring.userId) &&
        Objects.equals(this.counter, monitoring.counter) &&
        Objects.equals(this.status, monitoring.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ID, datetime, event, fileId, userId, counter, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Monitoring {\n");
    
    sb.append("    ID: ").append(toIndentedString(ID)).append("\n");
    sb.append("    datetime: ").append(toIndentedString(datetime)).append("\n");
    sb.append("    event: ").append(toIndentedString(event)).append("\n");
    sb.append("    fileId: ").append(toIndentedString(fileId)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    counter: ").append(toIndentedString(counter)).append("\n");
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

