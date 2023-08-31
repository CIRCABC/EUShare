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
 * LastLog
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class LastLog   {
  @JsonProperty("id")
  private String id;

  @JsonProperty("email")
  private String email;

  @JsonProperty("name")
  private String name;

  @JsonProperty("username")
  private String username;

  @JsonProperty("total_space")
  private Integer totalSpace;

  @JsonProperty("last_logged")
  // Eushare
  @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
  
  @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime lastLogged;

  public LastLog id(String id) {
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

  public LastLog email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Get email
   * @return email
  */
  @ApiModelProperty(value = "")


  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public LastLog name(String name) {
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

  public LastLog username(String username) {
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

  public LastLog totalSpace(Integer totalSpace) {
    this.totalSpace = totalSpace;
    return this;
  }

  /**
   * Get totalSpace
   * @return totalSpace
  */
  @ApiModelProperty(value = "")


  public Integer getTotalSpace() {
    return totalSpace;
  }

  public void setTotalSpace(Integer totalSpace) {
    this.totalSpace = totalSpace;
  }

  public LastLog lastLogged(OffsetDateTime lastLogged) {
    this.lastLogged = lastLogged;
    return this;
  }

  /**
   * Get lastLogged
   * @return lastLogged
  */
  @ApiModelProperty(value = "")

  @Valid

  public OffsetDateTime getLastLogged() {
    return lastLogged;
  }

  public void setLastLogged(OffsetDateTime lastLogged) {
    this.lastLogged = lastLogged;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LastLog lastLog = (LastLog) o;
    return Objects.equals(this.id, lastLog.id) &&
        Objects.equals(this.email, lastLog.email) &&
        Objects.equals(this.name, lastLog.name) &&
        Objects.equals(this.username, lastLog.username) &&
        Objects.equals(this.totalSpace, lastLog.totalSpace) &&
        Objects.equals(this.lastLogged, lastLog.lastLogged);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, email, name, username, totalSpace, lastLogged);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LastLog {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    totalSpace: ").append(toIndentedString(totalSpace)).append("\n");
    sb.append("    lastLogged: ").append(toIndentedString(lastLogged)).append("\n");
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

