package eu.europa.circabc.eushare.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

// eushare
import com.fasterxml.jackson.annotation.JsonFormat;
/**
 * ApiKey
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class ApiKey   {
  @JsonProperty("apikey")
  private String apikey;

  public ApiKey apikey(String apikey) {
    this.apikey = apikey;
    return this;
  }

  /**
   * API Key
   * @return apikey
  */
  @ApiModelProperty(required = true, value = "API Key")
  @NotNull


  public String getApikey() {
    return apikey;
  }

  public void setApikey(String apikey) {
    this.apikey = apikey;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ApiKey apiKey = (ApiKey) o;
    return Objects.equals(this.apikey, apiKey.apikey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(apikey);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiKey {\n");
    
    sb.append("    apikey: ").append(toIndentedString(apikey)).append("\n");
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

