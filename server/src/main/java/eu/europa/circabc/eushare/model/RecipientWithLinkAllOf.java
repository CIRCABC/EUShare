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
 * RecipientWithLinkAllOf
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class RecipientWithLinkAllOf   {
  @JsonProperty("recipientId")
  private String recipientId;

  @JsonProperty("downloadLink")
  private String downloadLink;

  public RecipientWithLinkAllOf recipientId(String recipientId) {
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

  public RecipientWithLinkAllOf downloadLink(String downloadLink) {
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
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RecipientWithLinkAllOf recipientWithLinkAllOf = (RecipientWithLinkAllOf) o;
    return Objects.equals(this.recipientId, recipientWithLinkAllOf.recipientId) &&
        Objects.equals(this.downloadLink, recipientWithLinkAllOf.downloadLink);
  }

  @Override
  public int hashCode() {
    return Objects.hash(recipientId, downloadLink);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RecipientWithLinkAllOf {\n");
    
    sb.append("    recipientId: ").append(toIndentedString(recipientId)).append("\n");
    sb.append("    downloadLink: ").append(toIndentedString(downloadLink)).append("\n");
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

