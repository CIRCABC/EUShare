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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * UserSpace
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-04-10T14:56:31.271+02:00[Europe/Paris]")

public class UserSpace   {
  @JsonProperty("totalSpace")
  private BigDecimal totalSpace;

  @JsonProperty("usedSpace")
  private BigDecimal usedSpace;

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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserSpace userSpace = (UserSpace) o;
    return Objects.equals(this.totalSpace, userSpace.totalSpace) &&
        Objects.equals(this.usedSpace, userSpace.usedSpace);
  }

  @Override
  public int hashCode() {
    return Objects.hash(totalSpace, usedSpace);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserSpace {\n");
    
    sb.append("    totalSpace: ").append(toIndentedString(totalSpace)).append("\n");
    sb.append("    usedSpace: ").append(toIndentedString(usedSpace)).append("\n");
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

