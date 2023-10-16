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

import eu.europa.circabc.eushare.storage.entity.DBFile;
import eu.europa.circabc.eushare.storage.entity.DBFileDownloadRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FileDownloadRateRepository extends JpaRepository<DBFileDownloadRate, Integer> {

    Optional<DBFileDownloadRate> findByDateHourAndFile(LocalDateTime dateHour, DBFile file);

    List<DBFileDownloadRate> findByDateHour(LocalDateTime dateHour);

    List<DBFileDownloadRate> findByDateHourAfter(LocalDateTime dateHour);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO FileDownloadRate (dateHour, fileID, downloadCount) VALUES (DATE_FORMAT(:currentHour, '%Y-%m-%d %H:00:00'), :fileId, 1)", nativeQuery = true)
    void createEntryWithHourPrecision(@Param("currentHour") LocalDateTime currentHour, @Param("fileId") String fileId);

    void deleteByDateHourBefore(LocalDateTime date);

}
