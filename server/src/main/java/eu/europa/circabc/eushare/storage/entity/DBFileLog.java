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
import eu.europa.circabc.eushare.storage.dto.LastLoginDTO;
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
@NamedNativeQuery(name = "DBFileLog.getAllLastLogins", query = "SELECT id, email, name, username, total_space, last_logged, creation_date, uploads, status FROM users order by last_logged", resultSetMapping = "getLastLoginsMapping")

@NamedNativeQuery(name = "DBFileLog.getAllLastUploads", query = "SELECT u.email as uploader_email, s.email as share_email, f.filename, f.file_size, f.path, f.status, f.created, s.shorturl, s.download_notification "
    +
    "FROM shares s, files f, users u " +
    "WHERE f.uploader_id = u.id AND s.file_file_id = f.file_id order by f.created desc", resultSetMapping = "getLastUploadsMapping")

@NamedNativeQuery(name = "DBFileLog.getAllLastDownloads", query = "SELECT u.email as uploader_email, l.recipient, f.filename, f.path, f.password, s.shorturl, s.download_notification, l.download_date "
    +
    "FROM shares s, users u, logs l, files f " +
    "WHERE l.file_file_id = s.file_file_id AND f.file_id = l.file_file_id AND u.id = f.uploader_id AND l.recipient = s.email AND l.download_link = s.shorturl order by l.download_date desc", resultSetMapping = "getLastDownloadsMapping")

@NamedNativeQuery(name = "DBFileLog.getLastLogins", query = "SELECT id, email, name, username, total_space, last_logged, creation_date, uploads, status FROM users "
    +
    "ORDER BY " +
    "CASE WHEN :sortField = 'email' AND :sortOrder = 'ASC' THEN email END ASC, " +
    "CASE WHEN :sortField = 'name' AND :sortOrder = 'ASC' THEN name END ASC, " +
    "CASE WHEN :sortField = 'username' AND :sortOrder = 'ASC' THEN username END ASC, " +
    "CASE WHEN :sortField = 'total_space' AND :sortOrder = 'ASC' THEN total_space END ASC, " +
    "CASE WHEN :sortField = 'last_logged' AND :sortOrder = 'ASC' THEN last_logged END ASC, " +
    "CASE WHEN :sortField = 'creation_date' AND :sortOrder = 'ASC' THEN creation_date END ASC, " +
    "CASE WHEN :sortField = 'uploads' AND :sortOrder = 'ASC' THEN uploads END ASC, " +
    "CASE WHEN :sortField = 'status' AND :sortOrder = 'ASC' THEN status END ASC, " +
    "CASE WHEN :sortField = 'email' AND :sortOrder = 'DESC' THEN email END DESC, " +
    "CASE WHEN :sortField = 'name' AND :sortOrder = 'DESC' THEN name END DESC, " +
    "CASE WHEN :sortField = 'username' AND :sortOrder = 'DESC' THEN username END DESC, " +
    "CASE WHEN :sortField = 'total_space' AND :sortOrder = 'DESC' THEN total_space END DESC, " +
    "CASE WHEN :sortField = 'last_logged' AND :sortOrder = 'DESC' THEN last_logged END DESC, " +
    "CASE WHEN :sortField = 'creation_date' AND :sortOrder = 'DESC' THEN creation_date END DESC, " +
    "CASE WHEN :sortField = 'uploads' AND :sortOrder = 'DESC' THEN uploads END DESC, " +
    "CASE WHEN :sortField = 'status' AND :sortOrder = 'DESC' THEN status END DESC", resultSetMapping = "getLastLoginsMapping")

@SqlResultSetMapping(name = "getLastLoginsMapping", classes = @ConstructorResult(targetClass = LastLoginDTO.class, columns = {
    @ColumnResult(name = "id"),
    @ColumnResult(name = "email"),
    @ColumnResult(name = "name"),
    @ColumnResult(name = "username"),
    @ColumnResult(name = "total_space", type = Long.class),
    @ColumnResult(name = "last_logged", type = LocalDateTime.class),
    @ColumnResult(name = "creation_date", type = LocalDateTime.class),
    @ColumnResult(name = "uploads", type = Integer.class),
    @ColumnResult(name = "status")
}))

@NamedNativeQuery(name = "DBFileLog.getLastUploads", query = "SELECT u.email as uploader_email, s.email as share_email, f.filename, f.file_size, f.path, f.status, f.created, s.shorturl, s.download_notification "
    +
    "FROM shares s, files f, users u " +
    "WHERE f.uploader_id = u.id AND s.file_file_id = f.file_id " +
    "ORDER BY " +
    "CASE WHEN :sortField = 'uploader_email' AND :sortOrder = 'ASC' THEN u.email END ASC, " +
    "CASE WHEN :sortField = 'share_email' AND :sortOrder = 'ASC' THEN s.email END ASC, " +
    "CASE WHEN :sortField = 'filename' AND :sortOrder = 'ASC' THEN f.filename END ASC, " +
    "CASE WHEN :sortField = 'file_size' AND :sortOrder = 'ASC' THEN f.file_size END ASC, " +
    "CASE WHEN :sortField = 'path' AND :sortOrder = 'ASC' THEN f.path END ASC, " +
    "CASE WHEN :sortField = 'status' AND :sortOrder = 'ASC' THEN f.status END ASC, " +
    "CASE WHEN :sortField = 'created' AND :sortOrder = 'ASC' THEN f.created END ASC, " +
    "CASE WHEN :sortField = 'shorturl' AND :sortOrder = 'ASC' THEN s.shorturl END ASC, " +
    "CASE WHEN :sortField = 'download_notification' AND :sortOrder = 'ASC' THEN s.download_notification END ASC, " +
    "CASE WHEN :sortField = 'uploader_email' AND :sortOrder = 'DESC' THEN u.email END DESC, " +
    "CASE WHEN :sortField = 'share_email' AND :sortOrder = 'DESC' THEN s.email END DESC, " +
    "CASE WHEN :sortField = 'filename' AND :sortOrder = 'DESC' THEN f.filename END DESC, " +
    "CASE WHEN :sortField = 'file_size' AND :sortOrder = 'DESC' THEN f.file_size END DESC, " +
    "CASE WHEN :sortField = 'path' AND :sortOrder = 'DESC' THEN f.path END DESC, " +
    "CASE WHEN :sortField = 'status' AND :sortOrder = 'DESC' THEN f.status END DESC, " +
    "CASE WHEN :sortField = 'created' AND :sortOrder = 'DESC' THEN f.created END DESC, " +
    "CASE WHEN :sortField = 'shorturl' AND :sortOrder = 'DESC' THEN s.shorturl END DESC, " +
    "CASE WHEN :sortField = 'download_notification' AND :sortOrder = 'DESC' THEN s.download_notification END DESC", resultSetMapping = "getLastUploadsMapping")

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

@NamedNativeQuery(name = "DBFileLog.getLastDownloads", query = "SELECT u.email as uploader_email, l.recipient, f.filename, f.path, f.password, s.shorturl, s.download_notification, l.download_date "
    +
    "FROM shares s, users u, logs l, files f " +
    "WHERE l.file_file_id = f.file_id AND s.file_file_id = l.file_file_id AND u.id = f.uploader_id AND l.recipient = s.email AND l.download_link = s.shorturl "  +
    "ORDER BY " +
    "CASE WHEN :sortField = 'uploader_email' AND :sortOrder = 'ASC' THEN u.email END ASC, " +
    "CASE WHEN :sortField = 'recipient' AND :sortOrder = 'ASC' THEN l.recipient END ASC, " +
    "CASE WHEN :sortField = 'filename' AND :sortOrder = 'ASC' THEN f.filename END ASC, " +
    "CASE WHEN :sortField = 'path' AND :sortOrder = 'ASC' THEN f.path END ASC, " +
    "CASE WHEN :sortField = 'password' AND :sortOrder = 'ASC' THEN f.password END ASC, " +
    "CASE WHEN :sortField = 'shorturl' AND :sortOrder = 'ASC' THEN s.shorturl END ASC, " +
    "CASE WHEN :sortField = 'download_notification' AND :sortOrder = 'ASC' THEN s.download_notification END ASC, " +
    "CASE WHEN :sortField = 'download_date' AND :sortOrder = 'ASC' THEN l.download_date END ASC, " +
    "CASE WHEN :sortField = 'uploader_email' AND :sortOrder = 'DESC' THEN u.email END DESC, " +
    "CASE WHEN :sortField = 'recipient' AND :sortOrder = 'DESC' THEN l.recipient END DESC, " +
    "CASE WHEN :sortField = 'filename' AND :sortOrder = 'DESC' THEN f.filename END DESC, " +
    "CASE WHEN :sortField = 'path' AND :sortOrder = 'DESC' THEN f.path END DESC, " +
    "CASE WHEN :sortField = 'password' AND :sortOrder = 'DESC' THEN f.password END DESC, " +
    "CASE WHEN :sortField = 'shorturl' AND :sortOrder = 'DESC' THEN s.shorturl END DESC, " +
    "CASE WHEN :sortField = 'download_notification' AND :sortOrder = 'DESC' THEN s.download_notification END DESC, " +
    "CASE WHEN :sortField = 'download_date' AND :sortOrder = 'DESC' THEN l.download_date END DESC", resultSetMapping = "getLastDownloadsMapping")

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

@NamedNativeQuery(name = "DBFileLog.countLastLogins", query = "SELECT COUNT(*) as count FROM users", resultSetMapping = "countLastLoginsMapping")
@SqlResultSetMapping(name = "countLastLoginsMapping", columns = @ColumnResult(name = "count", type = Long.class))

@NamedNativeQuery(name = "DBFileLog.countLastUploads", query = "SELECT COUNT(*) as count FROM shares s, files f, users u WHERE f.uploader_id=u.id AND s.file_file_id = f.file_id", resultSetMapping = "countLastUploadsMapping")
@SqlResultSetMapping(name = "countLastUploadsMapping", columns = @ColumnResult(name = "count", type = Long.class))

@NamedNativeQuery(name = "DBFileLog.countLastDownloads", query = "SELECT COUNT(*) as count FROM shares s, users u, logs l, files f WHERE l.file_file_id=s.file_file_id AND f.file_id = l.file_file_id AND u.id = f.uploader_id", resultSetMapping = "countLastDownloadsMapping")
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
