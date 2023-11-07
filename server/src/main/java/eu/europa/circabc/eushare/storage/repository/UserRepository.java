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
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import eu.europa.circabc.eushare.storage.dto.AbuseReportDetailsDTO;
import eu.europa.circabc.eushare.storage.dto.UserInfoDTO;
import eu.europa.circabc.eushare.storage.entity.DBUser;
import eu.europa.circabc.eushare.storage.entity.DBUser.Role;

public interface UserRepository
    extends PagingAndSortingRepository<DBUser, String> {
  public List<DBUser> findAllById(String id);

  public DBUser findOneByUsername(String username);

  @Query("FROM DBUser u WHERE (u.email = lower(:email))")
  public DBUser findOneByEmailIgnoreCase(@Param("email") String email);

  public DBUser findOneByEmailIgnoreCaseAndRole(String email, DBUser.Role role);

  public DBUser findOneByName(String name);

  public DBUser findOneById(String id);

  public DBUser findOneByNameAndRole(String name, DBUser.Role role);

  public DBUser findOneByApiKey(String apiKey);

  public List<DBUser> findByEmailIgnoreCaseStartsWith(
      String start,
      Pageable page);

  @Modifying
  @Query("UPDATE DBUser u SET u.uploads = u.uploads + 1 WHERE u.id = :id")
  void incrementUploads(@Param("id") String userId);

  @Query("SELECT u FROM DBUser u LEFT JOIN DBMonitoring m ON u.id = m.userId WHERE u.role = :role AND u.uploads > :uploads AND m.userId IS NULL")
  public List<DBUser> findExternalUsersWithMoreThanUploadsNotMonitored(@Param("role") DBUser.Role role, @Param("uploads") int uploads);  

  @Query(name = "UserInfoDTO.findByEmailRoleInternalOrAdmin", nativeQuery = true)
  public List<UserInfoDTO> findByEmailRoleInternalOrAdmin(@Param("start") String start, Pageable page,
      @Param("sortBy") String sortBy);

  @Query(name = "UserInfoDTO.findAllByEmailRoleInternalOrAdmin", nativeQuery = true)
  public List<UserInfoDTO> findAllByEmailRoleInternalOrAdmin(@Param("start") String start, Pageable page,
      @Param("sortBy") String sortBy);

}
