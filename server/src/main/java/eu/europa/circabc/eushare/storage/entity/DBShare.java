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

import eu.europa.circabc.eushare.model.Recipient;

import java.security.SecureRandom;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "shares", indexes = @Index(name = "INDEX_SHARES", columnList = "email, shorturl"))
public class DBShare {

  private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
  public static final int SHORT_URL_LENGTH = 10;
  private static final SecureRandom RANDOM = new SecureRandom();

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  private String downloadId;

  @Column(nullable = false)
  private String email;

  @ManyToOne(optional = false)
  @JoinColumn(foreignKey = @ForeignKey(name = "Share_to_file"))
  private DBFile file;

  @Column(nullable = false, unique = true, length = 10)
  private String shorturl;

  @Column(nullable = false)
  private Boolean downloadNotification;

  private String message;

  public DBShare() {
  }

  public DBShare(String email, DBFile file, String message, Boolean downloadNotification) {
    this.email = email;
    this.file = file;
    if (message != null) {
      this.message = message;
    }
    this.downloadNotification = downloadNotification;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DBShare)) {
      return false;
    }
    DBShare other = (DBShare) o;
    return (downloadId != null && downloadId.equals(other.getDownloadId()));
  }

  @Override
  public int hashCode() {
    return downloadId.hashCode();
  }

  public Recipient toRecipient() {
    Recipient recipient = new Recipient();
    recipient.setEmail(this.getEmail());
    recipient.setShortUrl(this.getShorturl());
    recipient.setMessage(this.getMessage());
    recipient.setDownloadNotification(this.getDownloadNotification());
    return recipient;
  }

  public String getDownloadId() {
    return downloadId;
  }

  public void setDownloadId(String downloadId) {
    this.downloadId = downloadId;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String receiver) {
    this.email = receiver;
  }

  public DBFile getFile() {
    return file;
  }

  public void setFile(DBFile file) {
    this.file = file;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getShorturl() {
    return shorturl;
  }

  public void setShorturl(String shorturl) {
    this.shorturl = shorturl;
  }

  public Boolean getDownloadNotification() {
    return downloadNotification;
  }

  public String generateShortUrl() {
    StringBuilder sb = new StringBuilder(SHORT_URL_LENGTH);
    for (int i = 0; i < SHORT_URL_LENGTH; i++) {
      sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
    }
    return sb.toString();
  }

  public void setDownloadNotification(Boolean downloadNotification) {
    this.downloadNotification = downloadNotification;
  }
}
