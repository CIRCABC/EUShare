package eu.europa.circabc.eushare.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import eu.europa.circabc.eushare.model.UserInfoAllOf;
import eu.europa.circabc.eushare.model.UserSpace;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * UserInfo
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class UserInfo   {
  @JsonProperty("totalSpace")
  private BigDecimal totalSpace;

  @JsonProperty("usedSpace")
  private BigDecimal usedSpace;

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
        Objects.equals(this.id, userInfo.id) &&
        Objects.equals(this.loginUsername, userInfo.loginUsername) &&
        Objects.equals(this.givenName, userInfo.givenName) &&
        Objects.equals(this.email, userInfo.email) &&
        Objects.equals(this.isAdmin, userInfo.isAdmin);
  }

  @Override
  public int hashCode() {
    return Objects.hash(totalSpace, usedSpace, id, loginUsername, givenName, email, isAdmin);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserInfo {\n");
    
    sb.append("    totalSpace: ").append(toIndentedString(totalSpace)).append("\n");
    sb.append("    usedSpace: ").append(toIndentedString(usedSpace)).append("\n");
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

