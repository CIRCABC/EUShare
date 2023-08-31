package eu.europa.circabc.eushare.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.OffsetDateTime;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

// eushare
import com.fasterxml.jackson.annotation.JsonFormat;
/**
 * LastDownload
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class LastDownload   {
  @JsonProperty("uploader_email")
  private String uploaderEmail;

  @JsonProperty("recipient")
  private String recipient;

  @JsonProperty("filename")
  private String filename;

  @JsonProperty("path")
  private String path;

  @JsonProperty("password")
  private String password;

  @JsonProperty("shorturl")
  private String shorturl;

  @JsonProperty("download_notification")
  private Boolean downloadNotification;

  @JsonProperty("download_date")
  // Eushare
  @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
  
  @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime downloadDate;

  public LastDownload uploaderEmail(String uploaderEmail) {
    this.uploaderEmail = uploaderEmail;
    return this;
  }

  /**
   * Get uploaderEmail
   * @return uploaderEmail
  */
  @ApiModelProperty(value = "")


  public String getUploaderEmail() {
    return uploaderEmail;
  }

  public void setUploaderEmail(String uploaderEmail) {
    this.uploaderEmail = uploaderEmail;
  }

  public LastDownload recipient(String recipient) {
    this.recipient = recipient;
    return this;
  }

  /**
   * Get recipient
   * @return recipient
  */
  @ApiModelProperty(value = "")


  public String getRecipient() {
    return recipient;
  }

  public void setRecipient(String recipient) {
    this.recipient = recipient;
  }

  public LastDownload filename(String filename) {
    this.filename = filename;
    return this;
  }

  /**
   * Get filename
   * @return filename
  */
  @ApiModelProperty(value = "")


  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public LastDownload path(String path) {
    this.path = path;
    return this;
  }

  /**
   * Get path
   * @return path
  */
  @ApiModelProperty(value = "")


  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public LastDownload password(String password) {
    this.password = password;
    return this;
  }

  /**
   * Get password
   * @return password
  */
  @ApiModelProperty(value = "")


  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public LastDownload shorturl(String shorturl) {
    this.shorturl = shorturl;
    return this;
  }

  /**
   * Get shorturl
   * @return shorturl
  */
  @ApiModelProperty(value = "")


  public String getShorturl() {
    return shorturl;
  }

  public void setShorturl(String shorturl) {
    this.shorturl = shorturl;
  }

  public LastDownload downloadNotification(Boolean downloadNotification) {
    this.downloadNotification = downloadNotification;
    return this;
  }

  /**
   * Get downloadNotification
   * @return downloadNotification
  */
  @ApiModelProperty(value = "")


  public Boolean getDownloadNotification() {
    return downloadNotification;
  }

  public void setDownloadNotification(Boolean downloadNotification) {
    this.downloadNotification = downloadNotification;
  }

  public LastDownload downloadDate(OffsetDateTime downloadDate) {
    this.downloadDate = downloadDate;
    return this;
  }

  /**
   * Get downloadDate
   * @return downloadDate
  */
  @ApiModelProperty(value = "")

  @Valid

  public OffsetDateTime getDownloadDate() {
    return downloadDate;
  }

  public void setDownloadDate(OffsetDateTime downloadDate) {
    this.downloadDate = downloadDate;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LastDownload lastDownload = (LastDownload) o;
    return Objects.equals(this.uploaderEmail, lastDownload.uploaderEmail) &&
        Objects.equals(this.recipient, lastDownload.recipient) &&
        Objects.equals(this.filename, lastDownload.filename) &&
        Objects.equals(this.path, lastDownload.path) &&
        Objects.equals(this.password, lastDownload.password) &&
        Objects.equals(this.shorturl, lastDownload.shorturl) &&
        Objects.equals(this.downloadNotification, lastDownload.downloadNotification) &&
        Objects.equals(this.downloadDate, lastDownload.downloadDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uploaderEmail, recipient, filename, path, password, shorturl, downloadNotification, downloadDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LastDownload {\n");
    
    sb.append("    uploaderEmail: ").append(toIndentedString(uploaderEmail)).append("\n");
    sb.append("    recipient: ").append(toIndentedString(recipient)).append("\n");
    sb.append("    filename: ").append(toIndentedString(filename)).append("\n");
    sb.append("    path: ").append(toIndentedString(path)).append("\n");
    sb.append("    password: ").append(toIndentedString(password)).append("\n");
    sb.append("    shorturl: ").append(toIndentedString(shorturl)).append("\n");
    sb.append("    downloadNotification: ").append(toIndentedString(downloadNotification)).append("\n");
    sb.append("    downloadDate: ").append(toIndentedString(downloadDate)).append("\n");
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

