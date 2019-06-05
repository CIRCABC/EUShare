/**
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */

package com.circabc.easyshare.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.List;

public interface FileRepository extends PagingAndSortingRepository<DBFile, String> {
    List<DBFile> findByExpirationDateBefore(LocalDate date);

    List<DBFile> findByStatus(DBFile.Status status, Pageable page);

    // get the files a receiver can retrieve
    List<DBFile> findByStatusAndSharedWith_Receiver_Id(DBFile.Status status, String id, Pageable page);

    // get the files an uploader has uploaded
    List<DBFile> findByStatusAndUploader_Id(DBFile.Status status, String id, Pageable page);
}
