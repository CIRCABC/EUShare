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

public interface ShareRepository extends CrudRepository<DBShare, String> {
    DBShare findOneByDownloadId(String downloadId);
    DBShare findOneByShorturl(String shortUrl);
    List<DBShare> findByFile_id(String fileId);
    List<DBShare> findByEmail(String email);
    void deleteByDownloadId(String downloadId);
    void deleteByEmailAndFile_id(String email, String fileId);
}