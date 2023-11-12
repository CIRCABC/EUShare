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

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import eu.europa.circabc.eushare.storage.dto.LastDownloadDTO;
import eu.europa.circabc.eushare.storage.dto.LastLoginDTO;
import eu.europa.circabc.eushare.storage.dto.LastUploadDTO;
import eu.europa.circabc.eushare.storage.entity.DBFileLog;

public interface FileLogsRepository extends PagingAndSortingRepository<DBFileLog, String> {
    List<DBFileLog> findByFileId(String fileId);

    @Query(name = "DBFileLog.getAllLastLogins", nativeQuery = true)
    List<LastLoginDTO> getAllLastLogins();

    @Query(name = "DBFileLog.getAllLastUploads", nativeQuery = true)
    List<LastUploadDTO> getAllLastUploads();

    @Query(name = "DBFileLog.getAllLastDownloads", nativeQuery = true)
    List<LastDownloadDTO> getAllLastDownloads();

    @Query(name = "DBFileLog.getLastLogins", nativeQuery = true)
    List<LastLoginDTO> getLastLogins(Pageable pageable, @Param("sortField") String sortField,
            @Param("sortOrder") String sortOrder);

    @Query(name = "DBFileLog.getLastUploads", nativeQuery = true)
    List<LastUploadDTO> getLastUploads(Pageable pageable, @Param("sortField") String sortField,
            @Param("sortOrder") String sortOrder);

    @Query(name = "DBFileLog.getLastDownloads", nativeQuery = true)
    List<LastDownloadDTO> getLastDownloads(Pageable pageable, @Param("sortField") String sortField,
            @Param("sortOrder") String sortOrder);

    @Query(nativeQuery = true, name = "DBFileLog.countLastLogins")
    Long countLastLogins();

    @Query(nativeQuery = true, name = "DBFileLog.countLastUploads")
    Long countLastUploads();

    @Query(nativeQuery = true, name = "DBFileLog.countLastDownloads")
    Long countLastDownloads();
}
