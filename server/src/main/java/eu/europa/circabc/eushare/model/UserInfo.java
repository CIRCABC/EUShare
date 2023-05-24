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
import eu.europa.circabc.eushare.model.UserInfoAllOf;
import eu.europa.circabc.eushare.model.UserSpace;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

// eushare
import com.fasterxml.jackson.annotation.JsonFormat;
/**
 * UserInfo
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class UserInfo   {
  @JsonProperty("totalSpace")
  private BigDecimal totalSpace;

  @JsonProperty("usedSpace")
  private BigDecimal usedSpace;

  @JsonProperty("filesCount")
  private BigDecimal filesCount;

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

  public UserInfo totalSpace(BigDecimal totalSpace) {
    this.totalSpace = totalSpace;
    return this;
  }

  /**
   * Total space the user has (Bytes)
   * minimum: 0
   * @return totalSpace
  */
  @ApiModelProperty(required = true, value = "Total space the user has (Bytes)")
  @NotNull

  @Valid
@DecimalMin("0")
  public BigDecimal getTotalSpace() {
    return totalSpace;
  }

  public void setTotalSpace(BigDecimal totalSpace) {
    this.totalSpace = totalSpace;
  }

  public UserInfo usedSpace(BigDecimal usedSpace) {
    this.usedSpace = usedSpace;
    return this;
  }

  /**
   * Space the user already used (Bytes)
   * minimum: 0
   * @return usedSpace
  */
  @ApiModelProperty(required = true, value = "Space the user already used (Bytes)")
  @NotNull

  @Valid
@DecimalMin("0")
  public BigDecimal getUsedSpace() {
    return usedSpace;
  }

  public void setUsedSpace(BigDecimal usedSpace) {
    this.usedSpace = usedSpace;
  }

  public UserInfo filesCount(BigDecimal filesCount) {
    this.filesCount = filesCount;
    return this;
  }

  /**
   * Number of files
   * minimum: 0
   * @return filesCount
  */
  @ApiModelProperty(value = "Number of files")

  @Valid
@DecimalMin("0")
  public BigDecimal getFilesCount() {
    return filesCount;
  }

  public void setFilesCount(BigDecimal filesCount) {
    this.filesCount = filesCount;
  }

  public UserInfo id(String id) {
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

  public UserInfo loginUsername(String loginUsername) {
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

  public UserInfo givenName(String givenName) {
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

  public UserInfo email(String email) {
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

  public UserInfo role(RoleEnum role) {
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

  public UserInfo isAdmin(Boolean isAdmin) {
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


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserInfo userInfo = (UserInfo) o;
    return Objects.equals(this.totalSpace, userInfo.totalSpace) &&
        Objects.equals(this.usedSpace, userInfo.usedSpace) &&
        Objects.equals(this.filesCount, userInfo.filesCount) &&
        Objects.equals(this.id, userInfo.id) &&
        Objects.equals(this.loginUsername, userInfo.loginUsername) &&
        Objects.equals(this.givenName, userInfo.givenName) &&
        Objects.equals(this.email, userInfo.email) &&
        Objects.equals(this.role, userInfo.role) &&
        Objects.equals(this.isAdmin, userInfo.isAdmin);
  }

  @Override
  public int hashCode() {
    return Objects.hash(totalSpace, usedSpace, filesCount, id, loginUsername, givenName, email, role, isAdmin);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserInfo {\n");
    
    sb.append("    totalSpace: ").append(toIndentedString(totalSpace)).append("\n");
    sb.append("    usedSpace: ").append(toIndentedString(usedSpace)).append("\n");
    sb.append("    filesCount: ").append(toIndentedString(filesCount)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    loginUsername: ").append(toIndentedString(loginUsername)).append("\n");
    sb.append("    givenName: ").append(toIndentedString(givenName)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    role: ").append(toIndentedString(role)).append("\n");
    sb.append("    isAdmin: ").append(toIndentedString(isAdmin)).append("\n");
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

