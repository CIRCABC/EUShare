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
 * TrustRequest
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class TrustRequest   {
  @JsonProperty("id")
  private String id;

  @JsonProperty("description")
  private String description;

  @JsonProperty("email")
  private String email;

  @JsonProperty("approved")
  private Boolean approved;

  @JsonProperty("requestDateTime")
  // Eushare
  @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
  
  @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime requestDateTime;

  @JsonProperty("name")
  private String name;

  @JsonProperty("username")
  private String username;

  public TrustRequest id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  @ApiModelProperty(value = "")


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public TrustRequest description(String description) {
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

  public TrustRequest email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Get email
   * @return email
  */
  @ApiModelProperty(value = "")

@javax.validation.constraints.Email
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public TrustRequest approved(Boolean approved) {
    this.approved = approved;
    return this;
  }

  /**
   * Get approved
   * @return approved
  */
  @ApiModelProperty(value = "")


  public Boolean getApproved() {
    return approved;
  }

  public void setApproved(Boolean approved) {
    this.approved = approved;
  }

  public TrustRequest requestDateTime(OffsetDateTime requestDateTime) {
    this.requestDateTime = requestDateTime;
    return this;
  }

  /**
   * Get requestDateTime
   * @return requestDateTime
  */
  @ApiModelProperty(value = "")

  @Valid

  public OffsetDateTime getRequestDateTime() {
    return requestDateTime;
  }

  public void setRequestDateTime(OffsetDateTime requestDateTime) {
    this.requestDateTime = requestDateTime;
  }

  public TrustRequest name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
  */
  @ApiModelProperty(value = "")


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public TrustRequest username(String username) {
    this.username = username;
    return this;
  }

  /**
   * Get username
   * @return username
  */
  @ApiModelProperty(value = "")


  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TrustRequest trustRequest = (TrustRequest) o;
    return Objects.equals(this.id, trustRequest.id) &&
        Objects.equals(this.description, trustRequest.description) &&
        Objects.equals(this.email, trustRequest.email) &&
        Objects.equals(this.approved, trustRequest.approved) &&
        Objects.equals(this.requestDateTime, trustRequest.requestDateTime) &&
        Objects.equals(this.name, trustRequest.name) &&
        Objects.equals(this.username, trustRequest.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, description, email, approved, requestDateTime, name, username);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TrustRequest {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    approved: ").append(toIndentedString(approved)).append("\n");
    sb.append("    requestDateTime: ").append(toIndentedString(requestDateTime)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
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

