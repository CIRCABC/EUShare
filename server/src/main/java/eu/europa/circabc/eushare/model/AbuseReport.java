package eu.europa.circabc.eushare.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

// eushare
import com.fasterxml.jackson.annotation.JsonFormat;
/**
 * AbuseReport
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class AbuseReport   {
  @JsonProperty("ID")
  private String ID;

  @JsonProperty("reporter")
  private String reporter;

  @JsonProperty("fileId")
  private String fileId;

  @JsonProperty("reason")
  private String reason;

  @JsonProperty("description")
  private String description;

  @JsonProperty("date")
  // Eushare
  @JsonFormat(pattern = "yyyy-MM-dd")
  
  @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
  private LocalDate date;

  @JsonProperty("status")
  private Boolean status;

  public AbuseReport ID(String ID) {
    this.ID = ID;
    return this;
  }

  /**
   * Get ID
   * @return ID
  */
  @ApiModelProperty(value = "")


  public String getID() {
    return ID;
  }

  public void setID(String ID) {
    this.ID = ID;
  }

  public AbuseReport reporter(String reporter) {
    this.reporter = reporter;
    return this;
  }

  /**
   * Get reporter
   * @return reporter
  */
  @ApiModelProperty(value = "")


  public String getReporter() {
    return reporter;
  }

  public void setReporter(String reporter) {
    this.reporter = reporter;
  }

  public AbuseReport fileId(String fileId) {
    this.fileId = fileId;
    return this;
  }

  /**
   * Get fileId
   * @return fileId
  */
  @ApiModelProperty(value = "")


  public String getFileId() {
    return fileId;
  }

  public void setFileId(String fileId) {
    this.fileId = fileId;
  }

  public AbuseReport reason(String reason) {
    this.reason = reason;
    return this;
  }

  /**
   * Get reason
   * @return reason
  */
  @ApiModelProperty(value = "")


  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public AbuseReport description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
  */
  @ApiModelProperty(value = "")


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public AbuseReport date(LocalDate date) {
    this.date = date;
    return this;
  }

  /**
   * Get date
   * @return date
  */
  @ApiModelProperty(value = "")

  @Valid

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public AbuseReport status(Boolean status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
  */
  @ApiModelProperty(value = "")


  public Boolean getStatus() {
    return status;
  }

  public void setStatus(Boolean status) {
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
    AbuseReport abuseReport = (AbuseReport) o;
    return Objects.equals(this.ID, abuseReport.ID) &&
        Objects.equals(this.reporter, abuseReport.reporter) &&
        Objects.equals(this.fileId, abuseReport.fileId) &&
        Objects.equals(this.reason, abuseReport.reason) &&
        Objects.equals(this.description, abuseReport.description) &&
        Objects.equals(this.date, abuseReport.date) &&
        Objects.equals(this.status, abuseReport.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ID, reporter, fileId, reason, description, date, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AbuseReport {\n");
    
    sb.append("    ID: ").append(toIndentedString(ID)).append("\n");
    sb.append("    reporter: ").append(toIndentedString(reporter)).append("\n");
    sb.append("    fileId: ").append(toIndentedString(fileId)).append("\n");
    sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    date: ").append(toIndentedString(date)).append("\n");
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

