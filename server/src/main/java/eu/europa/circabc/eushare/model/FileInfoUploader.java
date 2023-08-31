package eu.europa.circabc.eushare.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import eu.europa.circabc.eushare.model.FileBasics;
import eu.europa.circabc.eushare.model.FileInfoUploaderAllOf;
import eu.europa.circabc.eushare.model.FileLog;
import eu.europa.circabc.eushare.model.Recipient;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

// eushare
import com.fasterxml.jackson.annotation.JsonFormat;
/**
 * FileInfoUploader
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class FileInfoUploader   {
  @JsonProperty("expirationDate")
  // Eushare
  @JsonFormat(pattern = "yyyy-MM-dd")
  
  @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
  private LocalDate expirationDate;

  @JsonProperty("hasPassword")
  private Boolean hasPassword;

  @JsonProperty("name")
  private String name;

  @JsonProperty("size")
  private BigDecimal size;

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
    
    DELETED("DELETED");

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

  public FileInfoUploader expirationDate(LocalDate expirationDate) {
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

  public FileInfoUploader hasPassword(Boolean hasPassword) {
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

  public FileInfoUploader name(String name) {
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

  public FileInfoUploader size(BigDecimal size) {
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

  public FileInfoUploader fileId(String fileId) {
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

  public FileInfoUploader sharedWith(List<Recipient> sharedWith) {
    this.sharedWith = sharedWith;
    return this;
  }

  public FileInfoUploader addSharedWithItem(Recipient sharedWithItem) {
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

  public FileInfoUploader fileLogs(List<FileLog> fileLogs) {
    this.fileLogs = fileLogs;
    return this;
  }

  public FileInfoUploader addFileLogsItem(FileLog fileLogsItem) {
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

  public FileInfoUploader status(StatusEnum status) {
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
    FileInfoUploader fileInfoUploader = (FileInfoUploader) o;
    return Objects.equals(this.expirationDate, fileInfoUploader.expirationDate) &&
        Objects.equals(this.hasPassword, fileInfoUploader.hasPassword) &&
        Objects.equals(this.name, fileInfoUploader.name) &&
        Objects.equals(this.size, fileInfoUploader.size) &&
        Objects.equals(this.fileId, fileInfoUploader.fileId) &&
        Objects.equals(this.sharedWith, fileInfoUploader.sharedWith) &&
        Objects.equals(this.fileLogs, fileInfoUploader.fileLogs) &&
        Objects.equals(this.status, fileInfoUploader.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(expirationDate, hasPassword, name, size, fileId, sharedWith, fileLogs, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FileInfoUploader {\n");
    
    sb.append("    expirationDate: ").append(toIndentedString(expirationDate)).append("\n");
    sb.append("    hasPassword: ").append(toIndentedString(hasPassword)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
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

