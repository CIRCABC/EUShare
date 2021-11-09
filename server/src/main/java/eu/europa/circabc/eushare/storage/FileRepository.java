/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.storage;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FileRepository extends PagingAndSortingRepository<DBFile, String> {
    List<DBFile> findByExpirationDateBefore(LocalDate date);

    DBFile findByStatusAndSharedWith_DownloadId(DBFile.Status status, String downloadId);

    DBFile findByStatusAndId(DBFile.Status status, String id);

    DBFile findOneById(String id);

    List<DBFile> findByStatus(DBFile.Status status, Pageable page);

    // get the files a receiver can retrieve
    List<DBFile> findByStatusAndSharedWith_Receiver(DBFile.Status status, String id, Pageable page);

    // get the files a receiver can retrieve with ordering by expiration date and file name
    List<DBFile> findByStatusAndSharedWith_ReceiverOrderByExpirationDateAscFilenameAsc(DBFile.Status status, String id, Pageable page);

    // get the files an uploader has uploaded
    List<DBFile> findByStatusAndUploader_Id(DBFile.Status status, String id, Pageable page);

    // get the files an uploader has uploaded with ordering by expiration date and file name
    List<DBFile> findByStatusAndUploader_IdOrderByExpirationDateAscFilenameAsc(DBFile.Status status, String id, Pageable page);
}
