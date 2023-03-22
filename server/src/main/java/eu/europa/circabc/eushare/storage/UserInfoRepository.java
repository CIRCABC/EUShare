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
  // users, FILES WHERE files.uploader_id=users.id and email like
  // lower(concat(:start,'%')) order by used_space desc", nativeQuery = true)

  @Query(
    value = "SELECT id, role, username, name, email, total_space, sum(files.file_size) as used_space, count(*) as files_count FROM users, files WHERE files.uploader_id=users.id and (files.status='AVAILABLE' or files.status='ALLOCATED') and ( email like lower(concat(:start,'%')) or lower(name) like lower(concat(:start,'%'))  or lower(name) like lower(concat('% ',:start,'%')) ) GROUP BY username ",
    nativeQuery = true
  )
  public List<DBUserInfoProjection> findByEmailRoleInternalOrAdmin(
    @Param("start") String start,
    Pageable page
  );

  @Query(
    value = "SELECT  u.id, u.role, u.username, u.name, u.email, u.total_space, COALESCE(SUM(f.file_size), 0) AS used_space,COALESCE(count(*), 0)  AS files_count FROM users u LEFT JOIN files f ON f.uploader_id = u.id WHERE ( u.email LIKE LOWER(CONCAT(:start, '%')) OR LOWER(u.name) LIKE LOWER(CONCAT(:start, '%')) OR LOWER(u.name) LIKE LOWER(CONCAT('% ', :start, '%')) ) ",
    nativeQuery = true
  )
  public List<DBUserInfoProjection> findAllByEmailRoleInternalOrAdmin(
    @Param("start") String start,
    Pageable page
  );
}
