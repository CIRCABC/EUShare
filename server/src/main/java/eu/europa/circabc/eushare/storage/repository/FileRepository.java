/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.storage.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import eu.europa.circabc.eushare.storage.dto.LastDownloadDTO;
import eu.europa.circabc.eushare.storage.dto.LastLoginDTO;
import eu.europa.circabc.eushare.storage.dto.LastUploadDTO;
import eu.europa.circabc.eushare.storage.entity.DBFile;
import eu.europa.circabc.eushare.storage.entity.DBFile.Status;

public interface FileRepository
  extends PagingAndSortingRepository<DBFile, String> {
  List<DBFile> findByExpirationDateBefore(LocalDate date);

  List<DBFile> findByUploaderId(String uploaderId);

  DBFile findByStatusAndSharedWithDownloadId(
    DBFile.Status status,
    String downloadId
  );

  DBFile findByStatusAndSharedWithShorturl(
    DBFile.Status status,
    String shortUrl
  );

  DBFile findByStatusAndId(DBFile.Status status, String id);

  DBFile findOneById(String id);

  List<DBFile> findByStatus(DBFile.Status status, Pageable page);

  // get the files a receiver can retrieve
  List<DBFile> findByStatusAndSharedWithEmail(
    DBFile.Status status,
    String id,
    Pageable page
  );

  // get the files a receiver can retrieve with ordering by expiration date and file name
  List<DBFile> findByStatusAndSharedWithEmailOrderByExpirationDateAscFilenameAsc(
    DBFile.Status status,
    String id,
    Pageable page
  );

  // get the files an uploader has uploaded
  List<DBFile> findByStatusAndUploaderId(
    DBFile.Status status,
    String id,
    Pageable page
  );

  // get the files an uploader has uploaded with ordering by expiration date and file name
  List<DBFile> findByStatusInAndUploaderIdOrderByExpirationDateAscFilenameAsc(
    List<DBFile.Status> status,
    String id,
    Pageable page
  );

  List<DBFile> findByStatusAndLastModifiedBefore(
    DBFile.Status status,
    LocalDateTime time
  );


 
}
