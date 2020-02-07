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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * FileBasics
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-11-05T16:07:50.538+01:00[Europe/Paris]")

public class FileBasics   {
  @JsonProperty("expirationDate")
  @JsonFormat(pattern="yyyy-MM-dd")
  private LocalDate expirationDate;

  @JsonProperty("hasPassword")
  private Boolean hasPassword;

  @JsonProperty("name")
  private String name;

  @JsonProperty("size")
  private BigDecimal size;

  public FileBasics expirationDate(LocalDate expirationDate) {
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

  public FileBasics hasPassword(Boolean hasPassword) {
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

  public FileBasics name(String name) {
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

  public FileBasics size(BigDecimal size) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FileBasics fileBasics = (FileBasics) o;
    return Objects.equals(this.expirationDate, fileBasics.expirationDate) &&
        Objects.equals(this.hasPassword, fileBasics.hasPassword) &&
        Objects.equals(this.name, fileBasics.name) &&
        Objects.equals(this.size, fileBasics.size);
  }

  @Override
  public int hashCode() {
    return Objects.hash(expirationDate, hasPassword, name, size);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FileBasics {\n");
    
    sb.append("    expirationDate: ").append(toIndentedString(expirationDate)).append("\n");
    sb.append("    hasPassword: ").append(toIndentedString(hasPassword)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
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

