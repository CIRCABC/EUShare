/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.storage.entity;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import eu.europa.circabc.eushare.model.TrustLog;
import eu.europa.circabc.eushare.model.TrustLog.OriginEnum;
import io.swagger.annotations.ApiModel;

@Entity
@Table(name = "TrustLog")
@ApiModel(description = "DBTrustLog")
public class DBTrustLog {
  
  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @JsonProperty("id")
  private String id;

  @JsonProperty("trustDate")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private OffsetDateTime trustDate;

  @JsonProperty("truster")
  private String truster;

  @Enumerated(EnumType.STRING)
  @JsonProperty("origin")
  private Origin origin;

  public enum Origin {
    REQUEST, SHARE, AUTO
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public OffsetDateTime getTrustDate() {
    return trustDate;
  }

  public void setTrustDate(OffsetDateTime trustDate) {
    this.trustDate = trustDate;
  }

  public String getTruster() {
    return truster;
  }

  public void setTruster(String truster) {
    this.truster = truster;
  }

  public Origin getOrigin() {
    return origin;
  }

  public void setOrigin(Origin origin) {
    this.origin = origin;
  }

    public TrustLog toTrustLog() {
        TrustLog trustLog = new TrustLog();
        trustLog.setId(UUID.fromString(this.getId())); 
        trustLog.setTrustDate(this.getTrustDate());
        trustLog.setTruster(this.getTruster());
        trustLog.setOrigin(OriginEnum.valueOf(this.getOrigin().name()));
        return trustLog;
    }


    public static DBTrustLog fromTrustLog(TrustLog trustLog) {
        DBTrustLog dbTrustLog = new DBTrustLog();
        dbTrustLog.setTrustDate(trustLog.getTrustDate());
        dbTrustLog.setTruster(trustLog.getTruster());
        dbTrustLog.setOrigin(Origin.valueOf(trustLog.getOrigin().name())); 
        return dbTrustLog;
    }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DBTrustLog that = (DBTrustLog) o;
    return Objects.equals(id, that.id) &&
           Objects.equals(trustDate, that.trustDate) &&
           Objects.equals(truster, that.truster) &&
           origin == that.origin;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, trustDate, truster, origin);
  }

  @Override
  public String toString() {
    return "DBTrustLog{" +
           "id='" + id + '\'' +
           ", trustDate=" + trustDate +
           ", truster='" + truster + '\'' +
           ", origin=" + origin +
           '}';
  }
}
