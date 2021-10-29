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
import eu.europa.circabc.eushare.model.RecipientWithLink;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * FileInfoUploaderAllOf
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class FileInfoUploaderAllOf   {
  @JsonProperty("fileId")
  private String fileId;

  @JsonProperty("sharedWith")
  @Valid
  private List<RecipientWithLink> sharedWith = new ArrayList<>();

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

  public FileInfoUploaderAllOf sharedWith(List<RecipientWithLink> sharedWith) {
    this.sharedWith = sharedWith;
    return this;
  }

  public FileInfoUploaderAllOf addSharedWithItem(RecipientWithLink sharedWithItem) {
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
  public List<RecipientWithLink> getSharedWith() {
    return sharedWith;
  }

  public void setSharedWith(List<RecipientWithLink> sharedWith) {
    this.sharedWith = sharedWith;
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
        Objects.equals(this.sharedWith, fileInfoUploaderAllOf.sharedWith);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fileId, sharedWith);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FileInfoUploaderAllOf {\n");
    
    sb.append("    fileId: ").append(toIndentedString(fileId)).append("\n");
    sb.append("    sharedWith: ").append(toIndentedString(sharedWith)).append("\n");
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

