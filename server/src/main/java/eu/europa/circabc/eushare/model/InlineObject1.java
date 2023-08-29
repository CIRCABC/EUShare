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
import java.time.LocalDate;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

// eushare
import com.fasterxml.jackson.annotation.JsonFormat;
/**
 * InlineObject1
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class InlineObject1   {
  @JsonProperty("reporter")
  private String reporter;

  @JsonProperty("filedID")
  private String filedID;

  @JsonProperty("reason")
  private String reason;

  @JsonProperty("description")
  private String description;

  @JsonProperty("date")
  // Eushare
  @JsonFormat(pattern = "yyyy-MM-dd")
  
  @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
  private LocalDate date;

  @JsonProperty("status")
  private Boolean status;

  public InlineObject1 reporter(String reporter) {
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

  public InlineObject1 filedID(String filedID) {
    this.filedID = filedID;
    return this;
  }

  /**
   * Get filedID
   * @return filedID
  */
  @ApiModelProperty(value = "")


  public String getFiledID() {
    return filedID;
  }

  public void setFiledID(String filedID) {
    this.filedID = filedID;
  }

  public InlineObject1 reason(String reason) {
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

  public InlineObject1 description(String description) {
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

  public InlineObject1 date(LocalDate date) {
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

  public InlineObject1 status(Boolean status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
  */
  @ApiModelProperty(value = "")


  public Boolean getStatus() {
    return status;
  }

  public void setStatus(Boolean status) {
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
    InlineObject1 inlineObject1 = (InlineObject1) o;
    return Objects.equals(this.reporter, inlineObject1.reporter) &&
        Objects.equals(this.filedID, inlineObject1.filedID) &&
        Objects.equals(this.reason, inlineObject1.reason) &&
        Objects.equals(this.description, inlineObject1.description) &&
        Objects.equals(this.date, inlineObject1.date) &&
        Objects.equals(this.status, inlineObject1.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(reporter, filedID, reason, description, date, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InlineObject1 {\n");
    
    sb.append("    reporter: ").append(toIndentedString(reporter)).append("\n");
    sb.append("    filedID: ").append(toIndentedString(filedID)).append("\n");
    sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    date: ").append(toIndentedString(date)).append("\n");
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

