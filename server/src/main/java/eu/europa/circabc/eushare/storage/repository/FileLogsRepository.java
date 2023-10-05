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

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import eu.europa.circabc.eushare.storage.dto.LastDownloadDTO;
import eu.europa.circabc.eushare.storage.dto.LastLogDTO;
import eu.europa.circabc.eushare.storage.dto.LastUploadDTO;
import eu.europa.circabc.eushare.storage.entity.DBFileLog;

public interface FileLogsRepository extends CrudRepository<DBFileLog, String> {
  List<DBFileLog> findByFileId(String fileId);

    @Query(name = "DBFileLog.getLastLogs",nativeQuery = true)
    List<LastLogDTO> getLastLogs();

    @Query(name = "DBFileLog.getLastUploads",nativeQuery = true)
    List<LastUploadDTO> getLastUploads();

    @Query(name = "DBFileLog.getLastDownloads",nativeQuery = true)
    List<LastDownloadDTO> getLastDownloads();
}
