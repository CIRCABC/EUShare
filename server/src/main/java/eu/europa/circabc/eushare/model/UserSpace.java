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
 * UserSpace
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class UserSpace   {
  @JsonProperty("totalSpace")
  private BigDecimal totalSpace;

  @JsonProperty("usedSpace")
  private BigDecimal usedSpace;

  @JsonProperty("filesCount")
  private BigDecimal filesCount;

  public UserSpace totalSpace(BigDecimal totalSpace) {
    this.totalSpace = totalSpace;
    return this;
  }

  /**
   * Total space the user has (Bytes)
   * minimum: 0
   * @return totalSpace
  */
  @ApiModelProperty(required = true, value = "Total space the user has (Bytes)")
  @NotNull

  @Valid
@DecimalMin("0")
  public BigDecimal getTotalSpace() {
    return totalSpace;
  }

  public void setTotalSpace(BigDecimal totalSpace) {
    this.totalSpace = totalSpace;
  }

  public UserSpace usedSpace(BigDecimal usedSpace) {
    this.usedSpace = usedSpace;
    return this;
  }

  /**
   * Space the user already used (Bytes)
   * minimum: 0
   * @return usedSpace
  */
  @ApiModelProperty(required = true, value = "Space the user already used (Bytes)")
  @NotNull

  @Valid
@DecimalMin("0")
  public BigDecimal getUsedSpace() {
    return usedSpace;
  }

  public void setUsedSpace(BigDecimal usedSpace) {
    this.usedSpace = usedSpace;
  }

  public UserSpace filesCount(BigDecimal filesCount) {
    this.filesCount = filesCount;
    return this;
  }

  /**
   * Number of files
   * minimum: 0
   * @return filesCount
  */
  @ApiModelProperty(value = "Number of files")

  @Valid
@DecimalMin("0")
  public BigDecimal getFilesCount() {
    return filesCount;
  }

  public void setFilesCount(BigDecimal filesCount) {
    this.filesCount = filesCount;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserSpace userSpace = (UserSpace) o;
    return Objects.equals(this.totalSpace, userSpace.totalSpace) &&
        Objects.equals(this.usedSpace, userSpace.usedSpace) &&
        Objects.equals(this.filesCount, userSpace.filesCount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(totalSpace, usedSpace, filesCount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserSpace {\n");
    
    sb.append("    totalSpace: ").append(toIndentedString(totalSpace)).append("\n");
    sb.append("    usedSpace: ").append(toIndentedString(usedSpace)).append("\n");
    sb.append("    filesCount: ").append(toIndentedString(filesCount)).append("\n");
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

