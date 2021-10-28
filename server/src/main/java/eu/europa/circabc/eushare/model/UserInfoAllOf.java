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

  @JsonProperty("isAdmin")
  private Boolean isAdmin;

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
   * Abreviated user name used for login
   * @return loginUsername
  */
  @ApiModelProperty(required = true, value = "Abreviated user name used for login")
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
   * Email adress
   * @return email
  */
  @ApiModelProperty(required = true, value = "Email adress")
  @NotNull


  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
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
        Objects.equals(this.isAdmin, userInfoAllOf.isAdmin);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, loginUsername, givenName, email, isAdmin);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserInfoAllOf {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    loginUsername: ").append(toIndentedString(loginUsername)).append("\n");
    sb.append("    givenName: ").append(toIndentedString(givenName)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
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

