/**
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */

package com.circabc.easyshare.model;

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
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-11-05T16:07:50.538+01:00[Europe/Paris]")

public class Recipient   {
  @JsonProperty("emailOrName")
  private String emailOrName;

  @JsonProperty("message")
  private String message;

  @JsonProperty("sendEmail")
  private Boolean sendEmail;

  public Recipient emailOrName(String emailOrName) {
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

  public Recipient sendEmail(Boolean sendEmail) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Recipient recipient = (Recipient) o;
    return Objects.equals(this.emailOrName, recipient.emailOrName) &&
        Objects.equals(this.message, recipient.message) &&
        Objects.equals(this.sendEmail, recipient.sendEmail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(emailOrName, message, sendEmail);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Recipient {\n");
    
    sb.append("    emailOrName: ").append(toIndentedString(emailOrName)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    sendEmail: ").append(toIndentedString(sendEmail)).append("\n");
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

