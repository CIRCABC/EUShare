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
 * MountPointSpace
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class MountPointSpace   {
  @JsonProperty("path")
  private String path;

  @JsonProperty("totalSpace")
  private BigDecimal totalSpace;

  @JsonProperty("usableSpace")
  private BigDecimal usableSpace;

  public MountPointSpace path(String path) {
    this.path = path;
    return this;
  }

  /**
   * disk mount path
   * @return path
  */
  @ApiModelProperty(value = "disk mount path")


  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public MountPointSpace totalSpace(BigDecimal totalSpace) {
    this.totalSpace = totalSpace;
    return this;
  }

  /**
   * total space on disk
   * @return totalSpace
  */
  @ApiModelProperty(value = "total space on disk")

  @Valid

  public BigDecimal getTotalSpace() {
    return totalSpace;
  }

  public void setTotalSpace(BigDecimal totalSpace) {
    this.totalSpace = totalSpace;
  }

  public MountPointSpace usableSpace(BigDecimal usableSpace) {
    this.usableSpace = usableSpace;
    return this;
  }

  /**
   * usable space on disk
   * @return usableSpace
  */
  @ApiModelProperty(value = "usable space on disk")

  @Valid

  public BigDecimal getUsableSpace() {
    return usableSpace;
  }

  public void setUsableSpace(BigDecimal usableSpace) {
    this.usableSpace = usableSpace;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MountPointSpace mountPointSpace = (MountPointSpace) o;
    return Objects.equals(this.path, mountPointSpace.path) &&
        Objects.equals(this.totalSpace, mountPointSpace.totalSpace) &&
        Objects.equals(this.usableSpace, mountPointSpace.usableSpace);
  }

  @Override
  public int hashCode() {
    return Objects.hash(path, totalSpace, usableSpace);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MountPointSpace {\n");
    
    sb.append("    path: ").append(toIndentedString(path)).append("\n");
    sb.append("    totalSpace: ").append(toIndentedString(totalSpace)).append("\n");
    sb.append("    usableSpace: ").append(toIndentedString(usableSpace)).append("\n");
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

