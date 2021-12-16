package eu.europa.circabc.eushare.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

// eushare
import com.fasterxml.jackson.annotation.JsonFormat;
/**
 * FileInfoRecipientAllOf
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class FileInfoRecipientAllOf   {
  @JsonProperty("uploaderName")
  private String uploaderName;

  @JsonProperty("fileId")
  private String fileId;

  public FileInfoRecipientAllOf uploaderName(String uploaderName) {
    this.uploaderName = uploaderName;
    return this;
  }

  /**
   * name of the uploader
   * @return uploaderName
  */
  @ApiModelProperty(required = true, value = "name of the uploader")
  @NotNull


  public String getUploaderName() {
    return uploaderName;
  }

  public void setUploaderName(String uploaderName) {
    this.uploaderName = uploaderName;
  }

  public FileInfoRecipientAllOf fileId(String fileId) {
    this.fileId = fileId;
    return this;
  }

  /**
   * download file id
   * @return fileId
  */
  @ApiModelProperty(required = true, value = "download file id")
  @NotNull


  public String getFileId() {
    return fileId;
  }

  public void setFileId(String fileId) {
    this.fileId = fileId;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FileInfoRecipientAllOf fileInfoRecipientAllOf = (FileInfoRecipientAllOf) o;
    return Objects.equals(this.uploaderName, fileInfoRecipientAllOf.uploaderName) &&
        Objects.equals(this.fileId, fileInfoRecipientAllOf.fileId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uploaderName, fileId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FileInfoRecipientAllOf {\n");
    
    sb.append("    uploaderName: ").append(toIndentedString(uploaderName)).append("\n");
    sb.append("    fileId: ").append(toIndentedString(fileId)).append("\n");
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

