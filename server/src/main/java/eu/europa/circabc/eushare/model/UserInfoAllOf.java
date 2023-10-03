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
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

// eushare
import com.fasterxml.jackson.annotation.JsonFormat;
/**
 * UserInfoAllOf
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class UserInfoAllOf   {
  @JsonProperty("id")
  private String id;

  @JsonProperty("loginUsername")
  private String loginUsername;

  @JsonProperty("givenName")
  private String givenName;

  @JsonProperty("email")
  private String email;

  /**
   * role
   */
  public enum RoleEnum {
    ADMIN("ADMIN"),
    
    INTERNAL("INTERNAL"),
    
    EXTERNAL("EXTERNAL"),
    
    TRUSTED_EXTERNAL("TRUSTED_EXTERNAL"),
    
    API_KEY("API_KEY");

    private String value;

    RoleEnum(String value) {
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
    public static RoleEnum fromValue(String value) {
      for (RoleEnum b : RoleEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("role")
  private RoleEnum role;

  @JsonProperty("isAdmin")
  private Boolean isAdmin;

  /**
   * status = REGULAR (keep all user's rights) - Users who have full access to the site; SUSPENDED (Prevent users from uploading) - Users who can log in and download, but cannot upload; BANNED (prevent user from login) - Users who cannot log in at all.
   */
  public enum StatusEnum {
    REGULAR("REGULAR"),
    
    SUSPENDED("SUSPENDED"),
    
    BANNED("BANNED");

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

  public UserInfoAllOf id(String id) {
    this.id = id;
    return this;
  }

  /**
   * User ID
   * @return id
  */
  @ApiModelProperty(required = true, value = "User ID")
  @NotNull


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public UserInfoAllOf loginUsername(String loginUsername) {
    this.loginUsername = loginUsername;
    return this;
  }

  /**
   * Abbreviated user name used for login
   * @return loginUsername
  */
  @ApiModelProperty(required = true, value = "Abbreviated user name used for login")
  @NotNull


  public String getLoginUsername() {
    return loginUsername;
  }

  public void setLoginUsername(String loginUsername) {
    this.loginUsername = loginUsername;
  }

  public UserInfoAllOf givenName(String givenName) {
    this.givenName = givenName;
    return this;
  }

  /**
   * Full name of the user
   * @return givenName
  */
  @ApiModelProperty(value = "Full name of the user")


  public String getGivenName() {
    return givenName;
  }

  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  public UserInfoAllOf email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Email address
   * @return email
  */
  @ApiModelProperty(required = true, value = "Email address")
  @NotNull


  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public UserInfoAllOf role(RoleEnum role) {
    this.role = role;
    return this;
  }

  /**
   * role
   * @return role
  */
  @ApiModelProperty(value = "role")


  public RoleEnum getRole() {
    return role;
  }

  public void setRole(RoleEnum role) {
    this.role = role;
  }

  public UserInfoAllOf isAdmin(Boolean isAdmin) {
    this.isAdmin = isAdmin;
    return this;
  }

  /**
   * True if the user is admin
   * @return isAdmin
  */
  @ApiModelProperty(required = true, value = "True if the user is admin")
  @NotNull


  public Boolean getIsAdmin() {
    return isAdmin;
  }

  public void setIsAdmin(Boolean isAdmin) {
    this.isAdmin = isAdmin;
  }

  public UserInfoAllOf status(StatusEnum status) {
    this.status = status;
    return this;
  }

  /**
   * status = REGULAR (keep all user's rights) - Users who have full access to the site; SUSPENDED (Prevent users from uploading) - Users who can log in and download, but cannot upload; BANNED (prevent user from login) - Users who cannot log in at all.
   * @return status
  */
  @ApiModelProperty(value = "status = REGULAR (keep all user's rights) - Users who have full access to the site; SUSPENDED (Prevent users from uploading) - Users who can log in and download, but cannot upload; BANNED (prevent user from login) - Users who cannot log in at all.")


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
    UserInfoAllOf userInfoAllOf = (UserInfoAllOf) o;
    return Objects.equals(this.id, userInfoAllOf.id) &&
        Objects.equals(this.loginUsername, userInfoAllOf.loginUsername) &&
        Objects.equals(this.givenName, userInfoAllOf.givenName) &&
        Objects.equals(this.email, userInfoAllOf.email) &&
        Objects.equals(this.role, userInfoAllOf.role) &&
        Objects.equals(this.isAdmin, userInfoAllOf.isAdmin) &&
        Objects.equals(this.status, userInfoAllOf.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, loginUsername, givenName, email, role, isAdmin, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserInfoAllOf {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    loginUsername: ").append(toIndentedString(loginUsername)).append("\n");
    sb.append("    givenName: ").append(toIndentedString(givenName)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    role: ").append(toIndentedString(role)).append("\n");
    sb.append("    isAdmin: ").append(toIndentedString(isAdmin)).append("\n");
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

