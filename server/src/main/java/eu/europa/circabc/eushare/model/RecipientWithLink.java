/**
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */

package eu.europa.circabc.eushare.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;

/**
 * RecipientWithLink 
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-11-05T16:07:50.538+01:00[Europe/Paris]")

public class RecipientWithLink   {
  @JsonProperty("emailOrName")
  private String emailOrName;

  @JsonProperty("message")
  private String message;

  @JsonProperty("sendEmail")
  private Boolean sendEmail;

  @JsonProperty("recipientId")
  private String recipientId;

  @JsonProperty("downloadLink")
  private String downloadLink;

  public RecipientWithLink emailOrName(String emailOrName) {
    this.emailOrName = emailOrName;
    return this;
  }

  /**
   * Email or name of the recipient
   * @return emailOrName
  */
  @ApiModelProperty(required = true, value = "Email or name of the recipient")
  @NotNull


  public String getEmailOrName() {
    return emailOrName;
  }

  public void setEmailOrName(String emailOrName) {
    this.emailOrName = emailOrName;
  }

  public RecipientWithLink message(String message) {
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

  public RecipientWithLink sendEmail(Boolean sendEmail) {
    this.sendEmail = sendEmail;
    return this;
  }

  /**
   * True to send an email with the download link
   * @return sendEmail
  */
  @ApiModelProperty(required = true, value = "True to send an email with the download link")
  @NotNull


  public Boolean getSendEmail() {
    return sendEmail;
  }

  public void setSendEmail(Boolean sendEmail) {
    this.sendEmail = sendEmail;
  }

  public RecipientWithLink recipientId(String recipientId) {
    this.recipientId = recipientId;
    return this;
  }

  /**
   * Id of the recipient
   * @return recipientId
  */
  @ApiModelProperty(required = true, value = "Id of the recipient")
  @NotNull


  public String getRecipientId() {
    return recipientId;
  }

  public void setRecipientId(String recipientId) {
    this.recipientId = recipientId;
  }

  public RecipientWithLink downloadLink(String downloadLink) {
    this.downloadLink = downloadLink;
    return this;
  }

  /**
   * Download link to a specific file
   * @return downloadLink
  */
  @ApiModelProperty(required = true, value = "Download link to a specific file")
  @NotNull


  public String getDownloadLink() {
    return downloadLink;
  }

  public void setDownloadLink(String downloadLink) {
    this.downloadLink = downloadLink;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RecipientWithLink recipientWithLink = (RecipientWithLink) o;
    return Objects.equals(this.emailOrName, recipientWithLink.emailOrName) &&//NOSONAR
        Objects.equals(this.message, recipientWithLink.message) &&
        Objects.equals(this.sendEmail, recipientWithLink.sendEmail) &&
        Objects.equals(this.recipientId, recipientWithLink.recipientId) &&
        Objects.equals(this.downloadLink, recipientWithLink.downloadLink);
  }

  @Override
  public int hashCode() {
    return Objects.hash(emailOrName, message, sendEmail, recipientId, downloadLink);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RecipientWithLink {\n");
    
    sb.append("    emailOrName: ").append(toIndentedString(emailOrName)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    sendEmail: ").append(toIndentedString(sendEmail)).append("\n");
    sb.append("    recipientId: ").append(toIndentedString(recipientId)).append("\n");
    sb.append("    downloadLink: ").append(toIndentedString(downloadLink)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

