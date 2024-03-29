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

import eu.europa.circabc.eushare.model.EnumConverter;
import eu.europa.circabc.eushare.model.FileBasics;
import eu.europa.circabc.eushare.model.FileInfoRecipient;
import eu.europa.circabc.eushare.model.FileInfoUploader;
import eu.europa.circabc.eushare.model.FileLog;
import eu.europa.circabc.eushare.model.Recipient;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.crypto.bcrypt.BCrypt;

@Entity
@Table(
  name = "Files",
  indexes = {
    @Index(
      name = "INDEX_EXPIRATION_DATE",
      columnList = "expirationDate",
      unique = false
    ),
  }
)
public class DBFile {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(name = "FILE_ID", nullable = false, unique = true)
  private String id;

  @Column(nullable = false)
  private String filename;

  @Column(nullable = false)
  private String path;

  @Column(name = "fileSize", nullable = false)
  private long size;

  @Column(nullable = false)
  private LocalDate expirationDate;

  @Column(nullable = false)
  private LocalDateTime lastModified = LocalDateTime.now();

  @Column(nullable = true)
  private LocalDateTime created = LocalDateTime.now(); 

  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status = Status.ALLOCATED;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "file")
  private Set<DBShare> sharedWith;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "file")
  @OrderBy("downloadDate desc")
  private Set<DBFileLog> fileLogs;

  @ManyToOne(optional = false)
  @JoinColumn(foreignKey = @ForeignKey(name = "Fk_to_uploader"))
  private DBUser uploader;


  public DBFile(
    DBUser uploader,
    Collection<DBShare> sharedWith,
    String filename,
    long size,
    LocalDate expirationDate,
    String path
  ) {
    this(uploader, sharedWith, filename, size, expirationDate, path, null);
  }

  public DBFile(
    DBUser uploader,
    Collection<DBShare> sharedWith,
    String filename,
    long size,
    LocalDate expirationDate,
    String path,
    String password
  ) {
    this.uploader = uploader;
    this.sharedWith = new HashSet<>(sharedWith);
    this.filename = filename;
    this.size = size;
    this.expirationDate = expirationDate;
    this.path = path;

    if (password != null) {
      this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }
    this.fileLogs = new HashSet<>();
  }

  public DBFile() {}

  public FileInfoRecipient toFileInfoRecipient(String recipientEmail) {
    FileInfoRecipient fileInfoRecipient = new FileInfoRecipient();

    fileInfoRecipient.setExpirationDate(this.expirationDate);
    fileInfoRecipient.setHasPassword(this.password != null);
    fileInfoRecipient.setName(this.filename);
    fileInfoRecipient.setSize(new BigDecimal(this.size));
    fileInfoRecipient.setUploaderName(this.uploader.getName());

    for (DBShare dbShare : this.getSharedWith()) {
      if (dbShare.getEmail().equalsIgnoreCase(recipientEmail)) {
        fileInfoRecipient.setFileId(dbShare.getDownloadId());
      }
    }
    return fileInfoRecipient;
  }

  public FileInfoUploader toFileInfoUploader() {
    FileInfoUploader fileInfoUploader = new FileInfoUploader();
    List<Recipient> sharedWithRecipients =
      this.getSharedWith()
        .stream()
        .map(DBShare::toRecipient)
        .collect(Collectors.toList());
    List<FileLog> localFileLogs =
      this.getFileLogs()
        .stream()
        .map(DBFileLog::toFileLog)
        .collect(Collectors.toList());

    fileInfoUploader.setExpirationDate(this.expirationDate);
    fileInfoUploader.setHasPassword(this.password != null);
    fileInfoUploader.setName(this.filename);
    fileInfoUploader.setSize(new BigDecimal(this.size));
    fileInfoUploader.setFileId(this.getId());
    fileInfoUploader.setSharedWith(sharedWithRecipients);
    fileInfoUploader.setFileLogs(localFileLogs);

    FileInfoUploader.StatusEnum fileInfoUploaderStatus = EnumConverter.convert(this.status,
                        FileInfoUploader.StatusEnum.class);
    fileInfoUploader.setStatus(    fileInfoUploaderStatus );       


    return fileInfoUploader;
  }

  public FileBasics toFileBasics() {
    FileBasics fileBasics = new FileBasics();
    fileBasics.setExpirationDate(this.expirationDate);
    fileBasics.setHasPassword(this.password != null);
    fileBasics.setName(this.filename);
    fileBasics.setSize(new BigDecimal(this.size));
    return fileBasics;
  }

  public void setStatus(Status status) {
    this.status = status;
    this.lastModified = LocalDateTime.now();
  }

  public enum Status {
    ALLOCATED,
    UPLOADING,
    AVAILABLE,
    DELETED,
    FROZEN
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DBFile)) {
      return false;
    }
    DBFile other = (DBFile) o;
    return (id != null && id.equals(other.getId()));
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  public LocalDate getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(LocalDate expirationDate) {
    this.expirationDate = expirationDate;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public LocalDateTime getLastModified() {
    return lastModified;
  }

  public void setLastModified(LocalDateTime lastModified) {
    this.lastModified = lastModified;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public Set<DBShare> getSharedWith() {
    return sharedWith;
  }

  public void setSharedWith(Set<DBShare> sharedWith) {
    this.sharedWith = sharedWith;
  }

  public Set<DBFileLog> getFileLogs() {
    return fileLogs;
  }

  public void setFileLogs(Set<DBFileLog> fileLogs) {
    this.fileLogs = fileLogs;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public Status getStatus() {
    return status;
  }

  public DBUser getUploader() {
    return uploader;
  }

  public void setUploader(DBUser uploader) {
    this.uploader = uploader;
  }
}
