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

import org.springframework.data.repository.CrudRepository;

public interface UserFileRepository extends CrudRepository<DBUserFile, String> {
    DBUserFile findOneByDownloadId(String downloadId);
    void deleteByDownloadId(String downloadId);
    void deleteByReceiver_idAndFile_id(String receiverId, String fileId);
}