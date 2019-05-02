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
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;

/**
 * Recipient
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-04-10T14:56:31.271+02:00[Europe/Paris]")

public class Recipient   {
  @JsonProperty("emailOrID")
  private String emailOrID;

  @JsonProperty("message")
  private String message;

  public Recipient emailOrID(String emailOrID) {
    this.emailOrID = emailOrID;
    return this;
  }

  /**
   * User ID (email in case of external user)
   * @return emailOrID
  */
  @ApiModelProperty(required = true, value = "User ID (email in case of external user)")
  @NotNull


  public String getEmailOrID() {
    return emailOrID;
  }

  public void setEmailOrID(String emailOrID) {
    this.emailOrID = emailOrID;
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Recipient recipient = (Recipient) o;
    return Objects.equals(this.emailOrID, recipient.emailOrID) &&
        Objects.equals(this.message, recipient.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(emailOrID, message);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Recipient {\n");
    
    sb.append("    emailOrID: ").append(toIndentedString(emailOrID)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
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

