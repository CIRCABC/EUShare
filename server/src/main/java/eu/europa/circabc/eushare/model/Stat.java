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
 * Stat
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class Stat   {
  @JsonProperty("statId")
  private String statId;

  @JsonProperty("year")
  private BigDecimal year;

  @JsonProperty("month")
  private BigDecimal month;

  @JsonProperty("users")
  private BigDecimal users;

  @JsonProperty("downloads")
  private BigDecimal downloads;

  @JsonProperty("uploads")
  private BigDecimal uploads;

  @JsonProperty("downloadsData")
  private BigDecimal downloadsData;

  @JsonProperty("uploadsData")
  private BigDecimal uploadsData;

  public Stat statId(String statId) {
    this.statId = statId;
    return this;
  }

  /**
   * ID of the statistic record
   * @return statId
  */
  @ApiModelProperty(required = true, value = "ID of the statistic record")
  @NotNull


  public String getStatId() {
    return statId;
  }

  public void setStatId(String statId) {
    this.statId = statId;
  }

  public Stat year(BigDecimal year) {
    this.year = year;
    return this;
  }

  /**
   * year
   * @return year
  */
  @ApiModelProperty(required = true, value = "year")
  @NotNull

  @Valid

  public BigDecimal getYear() {
    return year;
  }

  public void setYear(BigDecimal year) {
    this.year = year;
  }

  public Stat month(BigDecimal month) {
    this.month = month;
    return this;
  }

  /**
   * month
   * @return month
  */
  @ApiModelProperty(required = true, value = "month")
  @NotNull

  @Valid

  public BigDecimal getMonth() {
    return month;
  }

  public void setMonth(BigDecimal month) {
    this.month = month;
  }

  public Stat users(BigDecimal users) {
    this.users = users;
    return this;
  }

  /**
   * users
   * @return users
  */
  @ApiModelProperty(required = true, value = "users")
  @NotNull

  @Valid

  public BigDecimal getUsers() {
    return users;
  }

  public void setUsers(BigDecimal users) {
    this.users = users;
  }

  public Stat downloads(BigDecimal downloads) {
    this.downloads = downloads;
    return this;
  }

  /**
   * number of downloads
   * @return downloads
  */
  @ApiModelProperty(required = true, value = "number of downloads")
  @NotNull

  @Valid

  public BigDecimal getDownloads() {
    return downloads;
  }

  public void setDownloads(BigDecimal downloads) {
    this.downloads = downloads;
  }

  public Stat uploads(BigDecimal uploads) {
    this.uploads = uploads;
    return this;
  }

  /**
   * number of uploads
   * @return uploads
  */
  @ApiModelProperty(required = true, value = "number of uploads")
  @NotNull

  @Valid

  public BigDecimal getUploads() {
    return uploads;
  }

  public void setUploads(BigDecimal uploads) {
    this.uploads = uploads;
  }

  public Stat downloadsData(BigDecimal downloadsData) {
    this.downloadsData = downloadsData;
    return this;
  }

  /**
   * total size of downloads
   * @return downloadsData
  */
  @ApiModelProperty(required = true, value = "total size of downloads")
  @NotNull

  @Valid

  public BigDecimal getDownloadsData() {
    return downloadsData;
  }

  public void setDownloadsData(BigDecimal downloadsData) {
    this.downloadsData = downloadsData;
  }

  public Stat uploadsData(BigDecimal uploadsData) {
    this.uploadsData = uploadsData;
    return this;
  }

  /**
   * total size of uploads
   * @return uploadsData
  */
  @ApiModelProperty(required = true, value = "total size of uploads")
  @NotNull

  @Valid

  public BigDecimal getUploadsData() {
    return uploadsData;
  }

  public void setUploadsData(BigDecimal uploadsData) {
    this.uploadsData = uploadsData;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Stat stat = (Stat) o;
    return Objects.equals(this.statId, stat.statId) &&
        Objects.equals(this.year, stat.year) &&
        Objects.equals(this.month, stat.month) &&
        Objects.equals(this.users, stat.users) &&
        Objects.equals(this.downloads, stat.downloads) &&
        Objects.equals(this.uploads, stat.uploads) &&
        Objects.equals(this.downloadsData, stat.downloadsData) &&
        Objects.equals(this.uploadsData, stat.uploadsData);
  }

  @Override
  public int hashCode() {
    return Objects.hash(statId, year, month, users, downloads, uploads, downloadsData, uploadsData);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Stat {\n");
    
    sb.append("    statId: ").append(toIndentedString(statId)).append("\n");
    sb.append("    year: ").append(toIndentedString(year)).append("\n");
    sb.append("    month: ").append(toIndentedString(month)).append("\n");
    sb.append("    users: ").append(toIndentedString(users)).append("\n");
    sb.append("    downloads: ").append(toIndentedString(downloads)).append("\n");
    sb.append("    uploads: ").append(toIndentedString(uploads)).append("\n");
    sb.append("    downloadsData: ").append(toIndentedString(downloadsData)).append("\n");
    sb.append("    uploadsData: ").append(toIndentedString(uploadsData)).append("\n");
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

