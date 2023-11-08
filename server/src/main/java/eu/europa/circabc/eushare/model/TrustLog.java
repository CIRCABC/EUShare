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
import java.time.OffsetDateTime;
import java.util.UUID;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

// eushare
import com.fasterxml.jackson.annotation.JsonFormat;
/**
 * TrustLog
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class TrustLog   {
  @JsonProperty("id")
  private UUID id;

  @JsonProperty("trustDate")
  // Eushare
  @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
  
  @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime trustDate;

  @JsonProperty("truster")
  private String truster;

  @JsonProperty("trusted")
  private String trusted;

  /**
   * The origin of the trust action.
   */
  public enum OriginEnum {
    REQUEST("REQUEST"),
    
    SHARE("SHARE"),
    
    AUTO("AUTO");

    private String value;

    OriginEnum(String value) {
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
    public static OriginEnum fromValue(String value) {
      for (OriginEnum b : OriginEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("origin")
  private OriginEnum origin;

  public TrustLog id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * The unique identifier for a trust log entry.
   * @return id
  */
  @ApiModelProperty(value = "The unique identifier for a trust log entry.")

  @Valid

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public TrustLog trustDate(OffsetDateTime trustDate) {
    this.trustDate = trustDate;
    return this;
  }

  /**
   * The date and time when the trust was logged.
   * @return trustDate
  */
  @ApiModelProperty(value = "The date and time when the trust was logged.")

  @Valid

  public OffsetDateTime getTrustDate() {
    return trustDate;
  }

  public void setTrustDate(OffsetDateTime trustDate) {
    this.trustDate = trustDate;
  }

  public TrustLog truster(String truster) {
    this.truster = truster;
    return this;
  }

  /**
   * The entity that is trusting.
   * @return truster
  */
  @ApiModelProperty(value = "The entity that is trusting.")


  public String getTruster() {
    return truster;
  }

  public void setTruster(String truster) {
    this.truster = truster;
  }

  public TrustLog trusted(String trusted) {
    this.trusted = trusted;
    return this;
  }

  /**
   * The entity that is trusting.
   * @return trusted
  */
  @ApiModelProperty(value = "The entity that is trusting.")


  public String getTrusted() {
    return trusted;
  }

  public void setTrusted(String trusted) {
    this.trusted = trusted;
  }

  public TrustLog origin(OriginEnum origin) {
    this.origin = origin;
    return this;
  }

  /**
   * The origin of the trust action.
   * @return origin
  */
  @ApiModelProperty(value = "The origin of the trust action.")


  public OriginEnum getOrigin() {
    return origin;
  }

  public void setOrigin(OriginEnum origin) {
    this.origin = origin;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TrustLog trustLog = (TrustLog) o;
    return Objects.equals(this.id, trustLog.id) &&
        Objects.equals(this.trustDate, trustLog.trustDate) &&
        Objects.equals(this.truster, trustLog.truster) &&
        Objects.equals(this.trusted, trustLog.trusted) &&
        Objects.equals(this.origin, trustLog.origin);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, trustDate, truster, trusted, origin);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TrustLog {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    trustDate: ").append(toIndentedString(trustDate)).append("\n");
    sb.append("    truster: ").append(toIndentedString(truster)).append("\n");
    sb.append("    trusted: ").append(toIndentedString(trusted)).append("\n");
    sb.append("    origin: ").append(toIndentedString(origin)).append("\n");
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

