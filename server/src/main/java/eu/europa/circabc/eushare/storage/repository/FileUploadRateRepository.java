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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import eu.europa.circabc.eushare.storage.entity.DBFileUploadRate;
import eu.europa.circabc.eushare.storage.entity.DBUser;

public interface FileUploadRateRepository extends JpaRepository<DBFileUploadRate, Integer> {

    Optional<DBFileUploadRate> findByDateHourAndUser(LocalDateTime dateHour, DBUser user);

    List<DBFileUploadRate> findByDateHour(LocalDateTime dateHour);

    List<DBFileUploadRate> findByDateHourAfter(LocalDateTime date);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO FileUploadRate (dateHour, userID, uploadCount) VALUES (DATE_FORMAT(:currentHour, '%Y-%m-%d %H:00:00'), :userId, 1)", nativeQuery = true)
    void createEntryWithHourPrecision(@Param("currentHour") LocalDateTime currentHour, @Param("userId") int userId);

    void deleteByDateHourBefore(LocalDateTime date);
}
