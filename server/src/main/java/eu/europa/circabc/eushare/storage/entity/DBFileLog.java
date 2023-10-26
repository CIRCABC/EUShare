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

import eu.europa.circabc.eushare.model.FileLog;
import eu.europa.circabc.eushare.storage.dto.LastDownloadDTO;
import eu.europa.circabc.eushare.storage.dto.LastLogDTO;
import eu.europa.circabc.eushare.storage.dto.LastUploadDTO;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "logs")
@NamedNativeQuery(name = "DBFileLog.getLastLogs", query = "SELECT id, email, name, username, total_space, last_logged, status FROM users order by last_logged desc", resultSetMapping = "getLastLogsMapping")
@SqlResultSetMapping(name = "getLastLogsMapping", classes = @ConstructorResult(targetClass = LastLogDTO.class, columns = {
    @ColumnResult(name = "id"),
    @ColumnResult(name = "email"),
    @ColumnResult(name = "name"),
    @ColumnResult(name = "username"),
    @ColumnResult(name = "total_space", type = Long.class),
    @ColumnResult(name = "last_logged", type = LocalDateTime.class),
    @ColumnResult(name = "status"),
}))
@NamedNativeQuery(name = "DBFileLog.getLastUploads", query = "SELECT u.email as uploader_email, s.email as share_email, f.filename, f.file_size, f.path, f.status, f.created, s.shorturl, s.download_notification FROM shares s, files f, users u WHERE f.uploader_id=u.id AND s.file_file_id = f.file_id ORDER BY f.created DESC", resultSetMapping = "getLastUploadsMapping")
@SqlResultSetMapping(name = "getLastUploadsMapping", classes = @ConstructorResult(targetClass = LastUploadDTO.class, columns = {
    @ColumnResult(name = "uploader_email", type = String.class),
    @ColumnResult(name = "share_email", type = String.class),
    @ColumnResult(name = "filename", type = String.class),
    @ColumnResult(name = "file_size", type = Long.class),
    @ColumnResult(name = "path", type = String.class),
    @ColumnResult(name = "status", type = String.class),
    @ColumnResult(name = "created", type = LocalDateTime.class),
    @ColumnResult(name = "shorturl", type = String.class),
    @ColumnResult(name = "download_notification", type = Boolean.class)

}))
@NamedNativeQuery(name = "DBFileLog.getLastDownloads", query = "SELECT u.email as uploader_email, l.recipient, f.filename, f.path, f.password, s.shorturl, s.download_notification, l.download_date FROM shares s, users u, logs l, files f WHERE l.file_file_id=s.file_file_id AND f.file_id = l.file_file_id AND u.id = f.uploader_id ORDER BY l.download_date DESC", resultSetMapping = "getLastDownloadsMapping")
@SqlResultSetMapping(name = "getLastDownloadsMapping", classes = @ConstructorResult(targetClass = LastDownloadDTO.class, columns = {
    @ColumnResult(name = "uploader_email", type = String.class),
    @ColumnResult(name = "recipient", type = String.class),
    @ColumnResult(name = "filename", type = String.class),
    @ColumnResult(name = "path", type = String.class),
    @ColumnResult(name = "password", type = String.class),
    @ColumnResult(name = "shorturl", type = String.class),
    @ColumnResult(name = "download_notification", type = Boolean.class),
    @ColumnResult(name = "download_date", type = LocalDateTime.class)

}))
@NamedNativeQuery(
    name = "DBFileLog.countLastLogs",
    query = "SELECT COUNT(*) as count FROM users",
    resultSetMapping = "countLastLogsMapping"
)
@SqlResultSetMapping(name = "countLastLogsMapping", columns = @ColumnResult(name = "count", type = Long.class))

@NamedNativeQuery(
    name = "DBFileLog.countLastUploads",
    query = "SELECT COUNT(*) as count FROM shares s, files f, users u WHERE f.uploader_id=u.id AND s.file_file_id = f.file_id",
    resultSetMapping = "countLastUploadsMapping"
)
@SqlResultSetMapping(name = "countLastUploadsMapping", columns = @ColumnResult(name = "count", type = Long.class))

@NamedNativeQuery(
    name = "DBFileLog.countLastDownloads",
    query = "SELECT COUNT(*) as count FROM shares s, users u, logs l, files f WHERE l.file_file_id=s.file_file_id AND f.file_id = l.file_file_id AND u.id = f.uploader_id",
    resultSetMapping = "countLastDownloadsMapping"
)
@SqlResultSetMapping(name = "countLastDownloadsMapping", columns = @ColumnResult(name = "count", type = Long.class))

public class DBFileLog {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(name = "LOG_ID", nullable = false, unique = true)
  private String id;

  @ManyToOne(optional = false)
  @JoinColumn(foreignKey = @ForeignKey(name = "Logs_to_file"))
  private DBFile file;

  @Column(nullable = false)
  private String recipient;

  @Column(nullable = false, length = 10)
  private String downloadLink;

  @Column(nullable = false)
  private LocalDateTime downloadDate;

  public DBFileLog(
      DBFile file,
      String recipient,
      LocalDateTime downloadDate,
      String downloadLink) {
    this.file = file;
    this.recipient = recipient;
    this.downloadDate = downloadDate;
    this.downloadLink = downloadLink;
  }

  private DBFileLog() {
  }

  public FileLog toFileLog() {
    FileLog fileLog = new FileLog();
    fileLog.setFileId(file.getId());
    final ZoneId zone = ZoneId.of("Europe/Paris");

    ZoneOffset zoneOffSet = zone.getRules().getOffset(downloadDate);
    OffsetDateTime offsetDateTime = downloadDate.atOffset(zoneOffSet);
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    fileLog.setDownloadDate(offsetDateTime.format(fmt));
    fileLog.setDownloadLink(downloadLink);
    fileLog.setRecipient(recipient);
    return fileLog;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((downloadDate == null) ? 0 : downloadDate.hashCode());
    result = prime * result + ((downloadLink == null) ? 0 : downloadLink.hashCode());
    result = prime * result + ((file == null) ? 0 : file.hashCode());
    result = prime * result + ((recipient == null) ? 0 : recipient.hashCode());

    return result;
  }

  @Override
  @SuppressWarnings("java:S3776")
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DBFileLog other = (DBFileLog) obj;
    if (downloadDate == null) {
      if (other.downloadDate != null)
        return false;
    } else if (!downloadDate.equals(other.downloadDate))
      return false;
    if (downloadLink == null) {
      if (other.downloadLink != null)
        return false;
    } else if (!downloadLink.equals(other.downloadLink))
      return false;
    if (file == null) {
      if (other.file != null)
        return false;
    } else if (!file.equals(other.file))
      return false;
    if (recipient == null) {
      if (other.recipient != null)
        return false;
    } else if (!recipient.equals(other.recipient))
      return false;

    return true;
  }

  public DBFile getFile() {
    return file;
  }

  public void setFile(DBFile file) {
    this.file = file;
  }

  public String getRecipient() {
    return recipient;
  }

  public void setRecipient(String recipient) {
    this.recipient = recipient;
  }

  public String getDownloadLink() {
    return downloadLink;
  }

  public void setDownloadLink(String downloadLink) {
    this.downloadLink = downloadLink;
  }

  public LocalDateTime getDownloadDate() {
    return downloadDate;
  }

  public void setDownloadDate(LocalDateTime downloadDate) {
    this.downloadDate = downloadDate;
  }

  @Override
  public String toString() {
    return ("DBFileLogs [downloadDate=" +
        downloadDate +
        ", downloadLink=" +
        downloadLink +
        ", file=" +
        file +
        ", recipient=" +
        recipient +
        "]");
  }
}
