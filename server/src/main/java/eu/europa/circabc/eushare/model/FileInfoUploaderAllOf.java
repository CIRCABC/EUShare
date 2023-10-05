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
import eu.europa.circabc.eushare.model.FileLog;
import eu.europa.circabc.eushare.model.Recipient;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

// eushare
import com.fasterxml.jackson.annotation.JsonFormat;
/**
 * FileInfoUploaderAllOf
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class FileInfoUploaderAllOf   {
  @JsonProperty("fileId")
  private String fileId;

  @JsonProperty("sharedWith")
  @Valid
  private List<Recipient> sharedWith = new ArrayList<>();

  @JsonProperty("fileLogs")
  @Valid
  private List<FileLog> fileLogs = new ArrayList<>();

  /**
   * Gets or Sets status
   */
  public enum StatusEnum {
    AVAILABLE("AVAILABLE"),
    
    ALLOCATED("ALLOCATED"),
    
    DELETED("DELETED"),
    
    FROZEN("FROZEN"),
    
    UPLOADING("UPLOADING");

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

  public FileInfoUploaderAllOf fileId(String fileId) {
    this.fileId = fileId;
    return this;
  }

  /**
   * file id
   * @return fileId
  */
  @ApiModelProperty(required = true, value = "file id")
  @NotNull


  public String getFileId() {
    return fileId;
  }

  public void setFileId(String fileId) {
    this.fileId = fileId;
  }

  public FileInfoUploaderAllOf sharedWith(List<Recipient> sharedWith) {
    this.sharedWith = sharedWith;
    return this;
  }

  public FileInfoUploaderAllOf addSharedWithItem(Recipient sharedWithItem) {
    this.sharedWith.add(sharedWithItem);
    return this;
  }

  /**
   * User IDs this file is shared with
   * @return sharedWith
  */
  @ApiModelProperty(required = true, value = "User IDs this file is shared with")
  @NotNull

  @Valid
@Size(min=1,max=10) 
  public List<Recipient> getSharedWith() {
    return sharedWith;
  }

  public void setSharedWith(List<Recipient> sharedWith) {
    this.sharedWith = sharedWith;
  }

  public FileInfoUploaderAllOf fileLogs(List<FileLog> fileLogs) {
    this.fileLogs = fileLogs;
    return this;
  }

  public FileInfoUploaderAllOf addFileLogsItem(FileLog fileLogsItem) {
    this.fileLogs.add(fileLogsItem);
    return this;
  }

  /**
   * File logs
   * @return fileLogs
  */
  @ApiModelProperty(required = true, value = "File logs")
  @NotNull

  @Valid

  public List<FileLog> getFileLogs() {
    return fileLogs;
  }

  public void setFileLogs(List<FileLog> fileLogs) {
    this.fileLogs = fileLogs;
  }

  public FileInfoUploaderAllOf status(StatusEnum status) {
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
    FileInfoUploaderAllOf fileInfoUploaderAllOf = (FileInfoUploaderAllOf) o;
    return Objects.equals(this.fileId, fileInfoUploaderAllOf.fileId) &&
        Objects.equals(this.sharedWith, fileInfoUploaderAllOf.sharedWith) &&
        Objects.equals(this.fileLogs, fileInfoUploaderAllOf.fileLogs) &&
        Objects.equals(this.status, fileInfoUploaderAllOf.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fileId, sharedWith, fileLogs, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FileInfoUploaderAllOf {\n");
    
    sb.append("    fileId: ").append(toIndentedString(fileId)).append("\n");
    sb.append("    sharedWith: ").append(toIndentedString(sharedWith)).append("\n");
    sb.append("    fileLogs: ").append(toIndentedString(fileLogs)).append("\n");
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

