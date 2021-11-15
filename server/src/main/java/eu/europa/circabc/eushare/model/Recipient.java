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
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Recipient
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class Recipient   {
  @JsonProperty("email")
  private String email;

  @JsonProperty("message")
  private String message;

  @JsonProperty("downloadLink")
  private String downloadLink;

  @JsonProperty("shortUrl")
  private String shortUrl;

  public Recipient email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Email of the recipient
   * @return email
  */
  @ApiModelProperty(value = "Email of the recipient")


  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Recipient message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Optional message to send
   * @return message
  */
  @ApiModelProperty(value = "Optional message to send")


  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Recipient downloadLink(String downloadLink) {
    this.downloadLink = downloadLink;
    return this;
  }

  /**
   * Download link to a specific file
   * @return downloadLink
  */
  @ApiModelProperty(value = "Download link to a specific file")


  public String getDownloadLink() {
    return downloadLink;
  }

  public void setDownloadLink(String downloadLink) {
    this.downloadLink = downloadLink;
  }

  public Recipient shortUrl(String shortUrl) {
    this.shortUrl = shortUrl;
    return this;
  }

  /**
   * Short link to a specific file
   * @return shortUrl
  */
  @ApiModelProperty(value = "Short link to a specific file")


  public String getShortUrl() {
    return shortUrl;
  }

  public void setShortUrl(String shortUrl) {
    this.shortUrl = shortUrl;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Recipient recipient = (Recipient) o;
    return Objects.equals(this.email, recipient.email) &&
        Objects.equals(this.message, recipient.message) &&
        Objects.equals(this.downloadLink, recipient.downloadLink) &&
        Objects.equals(this.shortUrl, recipient.shortUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(email, message, downloadLink, shortUrl);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Recipient {\n");
    
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    downloadLink: ").append(toIndentedString(downloadLink)).append("\n");
    sb.append("    shortUrl: ").append(toIndentedString(shortUrl)).append("\n");
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

