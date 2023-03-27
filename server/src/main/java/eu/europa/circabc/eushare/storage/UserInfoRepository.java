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
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface UserInfoRepository
    extends PagingAndSortingRepository<DBUserInfoProjection, String> {
  // @Query(value = "SELECT id, role, username, name, email, total_space,
  // sum(files.file_size) as used_space, count(files.file_id) as files_count FROM
  // users, FILES WHERE files.uploader_id=id and email like
  // lower(concat(:start,'%')) order by used_space desc", nativeQuery = true)

  @Query(value = "SELECT id, role, username, name, email, total_space, sum(files.file_size) as used_space, count(*) as files_count FROM users, files WHERE files.uploader_id=id and (files.status='AVAILABLE' or files.status='ALLOCATED') and ( email like lower(concat(:start,'%')) or lower(name) like lower(concat(:start,'%'))  or lower(name) like lower(concat('% ',:start,'%')) ) GROUP BY id, role, username, name, email, total_space ", nativeQuery = true)
  public List<DBUserInfoProjection> findByEmailRoleInternalOrAdmin(
      @Param("start") String start,
      Pageable page);

  @Query(value = "SELECT id, role, username, name, email, total_space, COALESCE(SUM(files.file_size), 0) AS used_space, COALESCE(COUNT(files.file_id), 0) AS files_count FROM users LEFT JOIN files ON files.uploader_id = id WHERE (email LIKE LOWER(CONCAT(:start,'%')) OR LOWER(name) LIKE LOWER(CONCAT(:start,'%')) OR LOWER(name) LIKE LOWER(CONCAT('% ',:start,'%'))) GROUP BY id, role, username, name, email, total_space ORDER BY name", nativeQuery = true)
  public List<DBUserInfoProjection> findAllByEmailRoleInternalOrAdmin(
      @Param("start") String start,
      Pageable page);
}
