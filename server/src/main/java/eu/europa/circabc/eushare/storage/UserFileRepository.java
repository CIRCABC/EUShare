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

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface UserFileRepository extends CrudRepository<DBUserFile, String> {
    DBUserFile findOneByDownloadId(String downloadId);
    List<DBUserFile> findByFile_id(String fileId);
    void deleteByDownloadId(String downloadId);
    void deleteByReceiverAndFile_id(String receiver, String fileId);
}