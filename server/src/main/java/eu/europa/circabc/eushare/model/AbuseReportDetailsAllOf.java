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
 * AbuseReportDetailsAllOf
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class AbuseReportDetailsAllOf   {
  @JsonProperty("filename")
  private String filename;

  @JsonProperty("filesize")
  private BigDecimal filesize;

  @JsonProperty("shortUrl")
  private String shortUrl;

  @JsonProperty("uploader_email")
  private String uploaderEmail;

  @JsonProperty("uploader_name")
  private String uploaderName;

  @JsonProperty("uploader_status")
  private String uploaderStatus;

  public AbuseReportDetailsAllOf filename(String filename) {
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

  public AbuseReportDetailsAllOf filesize(BigDecimal filesize) {
    this.filesize = filesize;
    return this;
  }

  /**
   * Get filesize
   * @return filesize
  */
  @ApiModelProperty(value = "")

  @Valid

  public BigDecimal getFilesize() {
    return filesize;
  }

  public void setFilesize(BigDecimal filesize) {
    this.filesize = filesize;
  }

  public AbuseReportDetailsAllOf shortUrl(String shortUrl) {
    this.shortUrl = shortUrl;
    return this;
  }

  /**
   * Get shortUrl
   * @return shortUrl
  */
  @ApiModelProperty(value = "")


  public String getShortUrl() {
    return shortUrl;
  }

  public void setShortUrl(String shortUrl) {
    this.shortUrl = shortUrl;
  }

  public AbuseReportDetailsAllOf uploaderEmail(String uploaderEmail) {
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

  public AbuseReportDetailsAllOf uploaderName(String uploaderName) {
    this.uploaderName = uploaderName;
    return this;
  }

  /**
   * Get uploaderName
   * @return uploaderName
  */
  @ApiModelProperty(value = "")


  public String getUploaderName() {
    return uploaderName;
  }

  public void setUploaderName(String uploaderName) {
    this.uploaderName = uploaderName;
  }

  public AbuseReportDetailsAllOf uploaderStatus(String uploaderStatus) {
    this.uploaderStatus = uploaderStatus;
    return this;
  }

  /**
   * Get uploaderStatus
   * @return uploaderStatus
  */
  @ApiModelProperty(value = "")


  public String getUploaderStatus() {
    return uploaderStatus;
  }

  public void setUploaderStatus(String uploaderStatus) {
    this.uploaderStatus = uploaderStatus;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AbuseReportDetailsAllOf abuseReportDetailsAllOf = (AbuseReportDetailsAllOf) o;
    return Objects.equals(this.filename, abuseReportDetailsAllOf.filename) &&
        Objects.equals(this.filesize, abuseReportDetailsAllOf.filesize) &&
        Objects.equals(this.shortUrl, abuseReportDetailsAllOf.shortUrl) &&
        Objects.equals(this.uploaderEmail, abuseReportDetailsAllOf.uploaderEmail) &&
        Objects.equals(this.uploaderName, abuseReportDetailsAllOf.uploaderName) &&
        Objects.equals(this.uploaderStatus, abuseReportDetailsAllOf.uploaderStatus);
  }

  @Override
  public int hashCode() {
    return Objects.hash(filename, filesize, shortUrl, uploaderEmail, uploaderName, uploaderStatus);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AbuseReportDetailsAllOf {\n");
    
    sb.append("    filename: ").append(toIndentedString(filename)).append("\n");
    sb.append("    filesize: ").append(toIndentedString(filesize)).append("\n");
    sb.append("    shortUrl: ").append(toIndentedString(shortUrl)).append("\n");
    sb.append("    uploaderEmail: ").append(toIndentedString(uploaderEmail)).append("\n");
    sb.append("    uploaderName: ").append(toIndentedString(uploaderName)).append("\n");
    sb.append("    uploaderStatus: ").append(toIndentedString(uploaderStatus)).append("\n");
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

